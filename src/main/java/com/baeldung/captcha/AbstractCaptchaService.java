package com.baeldung.captcha;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import com.baeldung.web.error.ReCaptchaInvalidException;

public abstract class AbstractCaptchaService implements ICaptchaService{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCaptchaService.class);
    
    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected CaptchaSettings captchaSettings;

    @Autowired
    protected ReCaptchaAttemptService reCaptchaAttemptService;

    @Autowired
    protected RestOperations restTemplate;

    protected static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    
    protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";
    
    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getSite();
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }
  

    protected void securityCheck(final String response) {
        LOGGER.debug("Attempting to validate response {}", response);

        if (reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }
    }

    protected boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    protected String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
