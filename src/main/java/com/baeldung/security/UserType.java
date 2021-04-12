package com.baeldung.security;

import com.baeldung.persistence.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

enum UserType {

    USER {
        @Override
        public String getHomePage(String username) {
            return "/homepage.html?user="+username;
        }
    },
    ADMIN {
        @Override
        public String getHomePage(String username) {
            return "/console";
        }
    },
    MANAGER {
        @Override
        public String getHomePage(String username) {
            return "/management";
        }
    };


    public abstract String getHomePage(String username) ;


    public static UserType findUserType(Collection<? extends GrantedAuthority> authorities) {
        boolean admin = false;
        boolean manager = false;
        boolean user = false;
        for (final GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("READ_PRIVILEGE")) {
                user = true;
            }
            if (grantedAuthority.getAuthority().equals("WRITE_PRIVILEGE")) {
                admin = true;
            }
            else if (grantedAuthority.getAuthority().equals("MANAGEMENT_PRIVILEGE")) {
                manager = true;
            }
        }
        if (manager) {
            return MANAGER;
        }
        else if (admin) {
            return ADMIN;
        }
        else if (user) {
            return USER;
        }
        return null;
    }

}
