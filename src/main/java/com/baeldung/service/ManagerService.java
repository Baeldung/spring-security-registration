package com.baeldung.service;

import com.baeldung.persistence.model.Role;
import com.baeldung.persistence.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService implements IManagerService {
    @Override
    public Optional<String> getAllManagers(User loggedInUser) {
        if(loggedInUser.getRoles().contains(new Role("ROLE_MANAGER"))) {
            return Optional.of("management");
        }
        return Optional.empty();
    }
}
