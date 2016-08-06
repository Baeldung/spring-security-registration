package org.baeldung.spring;

import org.baeldung.captcha.ICaptchaService;
import org.baeldung.web.error.ReCaptchaInvalidException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "org.baeldung.service" })
public class ServiceConfig {

    @Bean
    @ConditionalOnMissingBean(name = "captchaService")
    public ICaptchaService captchaService() {
        return new NoOpCaptchaService();
    }

    static class NoOpCaptchaService implements ICaptchaService {
        public NoOpCaptchaService() {}

        @Override
        public void processResponseToken(String responseToken) throws ReCaptchaInvalidException {}

        @Override
        public String getReCaptchaSite() {
            return null;
        }

        @Override
        public String getReCaptchaSecret() {
            return null;
        }
    }
}
