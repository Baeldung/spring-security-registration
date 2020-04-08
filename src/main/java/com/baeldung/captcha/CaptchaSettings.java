package com.baeldung.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {

    private String site;
    private String secret;
    
    //reCAPTCHA V3
    private String siteV3;
    private String secretV3;
    private float threshold;

    public CaptchaSettings() {
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSiteV3() {
        return siteV3;
    }

    public void setSiteV3(String siteV3) {
        this.siteV3 = siteV3;
    }

    public String getSecretV3() {
        return secretV3;
    }

    public void setSecretV3(String secretV3) {
        this.secretV3 = secretV3;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
}
