package com.baeldung.spring;

import com.baeldung.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration{
    // beans

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }

    @Bean
    public ClassLoaderTemplateResolver managementTemplateResolver() {
        ClassLoaderTemplateResolver managementTemplateResolver = new ClassLoaderTemplateResolver();
        managementTemplateResolver.setPrefix("private/");
        managementTemplateResolver.setSuffix(".html");
        managementTemplateResolver.setTemplateMode(TemplateMode.HTML);
        managementTemplateResolver.setCharacterEncoding("UTF-8");
        managementTemplateResolver.setOrder(1);
        managementTemplateResolver.setCheckExistence(true);
        return managementTemplateResolver;
    }
    
    
}