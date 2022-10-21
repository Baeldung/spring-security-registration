package com.baeldung.service;

import com.baeldung.persistence.model.User;

import java.util.Optional;

public interface IManagerService {
    Optional<String> getAllManagers(User loggedInUser);
}
