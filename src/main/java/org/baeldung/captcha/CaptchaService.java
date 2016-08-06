package org.baeldung.captcha;

import org.baeldung.web.error.ReCaptchaInvalidException;
import org.baeldung.web.error.ReCaptchaUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service("captchaService")
public class CaptchaService implements ICaptchaService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CaptchaSettings captchaSettings;

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;

    @Autowired
    private RestOperations restTemplate;

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Override
    public void processResponseToken(final String responseToken) {
        LOGGER.debug("Attempting to validate responseToken {}", responseToken);

        if(reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new ReCaptchaInvalidException("Client blocked");
        }

        if(!tokenSanityCheck(responseToken)) {
            throw new ReCaptchaInvalidException("Token invalid");
        }

        final URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", getReCaptchaSecret(), responseToken, getClientIP()));
        try {
            final GoogleResponse response = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            LOGGER.debug("Google's response: {} ", response.toString());

            if(!response.isSuccess()) {
                if(response.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch(RestClientException rce) {
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }

    private boolean tokenSanityCheck(final String responseToken) {
        return StringUtils.hasLength(responseToken) && TOKEN_PATTERN.matcher(responseToken).matches();
    }

    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getSite();
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
