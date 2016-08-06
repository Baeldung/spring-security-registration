package org.baeldung.captcha;

import org.baeldung.web.error.ReCaptchaInvalidException;

public interface ICaptchaService {
    void processResponseToken(final String responseToken) throws ReCaptchaInvalidException;

    String getReCaptchaSite();

    String getReCaptchaSecret();
}
