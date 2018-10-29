package org.baeldung.security;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("myAuthenticationSuccessHandler")
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    ActiveUserStore activeUserStore;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);

            String username;
            if (authentication.getPrincipal() instanceof User) {
                username = ((User) authentication.getPrincipal()).getEmail();
            } else {
                username = authentication.getName();
            }
            LoggedUser user = new LoggedUser(username, activeUserStore);
            session.setAttribute("user", user);
        }
        clearAuthenticationAttributes(request);
    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication) {
        switch (obtainUserRole(authentication)) {
        case "ROLE_USER":
            String username = (authentication.getPrincipal() instanceof User) ? ((User) authentication.getPrincipal()).getEmail() : authentication.getName();
            return "/homepage.html?user=" + username;
        case "ROLE_ADMIN":
            return "/console.html";
        case "ROLE_MANAGER":
            return "/management.html";
        default:
            throw new IllegalStateException();
        }
    }

    private String obtainUserRole(final Authentication authentication) {
        if (authentication.getPrincipal() instanceof User) {
            Collection<Role> userRoles = ((User) authentication.getPrincipal()).getRoles();
            if (userRoles.isEmpty())
                return null;
            return userRoles.iterator()
                .next()
                .getName();
        }
        Collection<String> privileges = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        return privileges.contains("MANAGER_PRIVILEGE") ? "ROLE_MANAGER" : privileges.contains("WRITE_PRIVILEGE") ? "ROLE_ADMIN" : privileges.contains("READ_PRIVILEGE") ? "ROLE_USER" : null;
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}