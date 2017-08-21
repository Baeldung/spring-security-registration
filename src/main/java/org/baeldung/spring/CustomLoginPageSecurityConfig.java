package org.baeldung.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//@Configuration
//@ComponentScan(basePackages = { "org.baeldung.security" })
//@EnableWebSecurity
public class CustomLoginPageSecurityConfig extends SecSecurityConfig {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        super.configure(http);
        http.formLogin().loginPage("/customLogin");
    }
}
