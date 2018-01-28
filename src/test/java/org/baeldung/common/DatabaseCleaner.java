package org.baeldung.common;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseCleaner {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public void clean() {
        log.info("Cleaning database");
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

}
