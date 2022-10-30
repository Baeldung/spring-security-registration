package com.baeldung.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.baeldung.persistence.model.User;

/**
 * Class to handle Access denied exceptions
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException deniedException) throws IOException {
        
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            final String user = ((User)auth.getPrincipal()).getEmail();
            LOGGER.warn(String.format("User %s attempted to access unauthorized URL: %s", user, httpServletRequest.getRequestURI()));
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/access-denied.html");
    }
}