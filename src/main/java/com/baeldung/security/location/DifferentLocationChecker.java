package com.baeldung.security.location;

import javax.servlet.http.HttpServletRequest;

import com.baeldung.persistence.model.NewLocationToken;
import com.baeldung.service.IUserService;
import com.baeldung.web.error.UnusualLocationException;
import com.baeldung.security.location.OnDifferentLocationLoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class DifferentLocationChecker implements UserDetailsChecker {

    @Autowired
    private IUserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void check(UserDetails userDetails) {
        final String ip = getClientIP();
        final NewLocationToken token = userService.isNewLoginLocation(userDetails.getUsername(), ip);
        if (token != null) {
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnDifferentLocationLoginEvent(request.getLocale(), userDetails.getUsername(), ip, token, appUrl));
            throw new UnusualLocationException("unusual location");
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
        // return "128.101.101.101"; // for testing United States
        // return "41.238.0.198"; // for testing Egypt
    }

}
