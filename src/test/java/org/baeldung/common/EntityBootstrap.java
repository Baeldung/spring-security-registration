package org.baeldung.common;

import org.baeldung.persistence.dao.PrivilegeRepository;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class EntityBootstrap {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public PrivilegeBuilder newPrivilege() {
        return new PrivilegeBuilder();
    }

    public RoleBuilder newRole() {
        return new RoleBuilder();
    }

    public UserBuilder newUser() {
        return new UserBuilder(randomAlphanumeric(8), randomAlphanumeric(16), random(8), random(8));
    }

    public VerificationTokenBuilder newVerificationToken(User user) {
        return new VerificationTokenBuilder(user);
    }

    public class PrivilegeBuilder {

        private Privilege privilege;

        private PrivilegeBuilder() {
            this.privilege = new Privilege();
        }

        public Privilege save() {
            log.info("Adding new Privilege {} to database", privilege);
            return privilegeRepository.save(privilege);
        }

        public PrivilegeBuilder withName(String name) {
            privilege.setName(name);
            return this;
        }

    }

    public class RoleBuilder {

        private Role role;

        private RoleBuilder() {
            role = new Role();
        }

        public RoleBuilder withName(String name) {
            role.setName(name);
            return this;
        }

        public Role save() {
            log.info("Adding new Role {} to database", role);
            return roleRepository.save(role);
        }

        public RoleBuilder withPrivileges(Privilege... privileges) {
            role.setPrivileges(asList(privileges));
            return this;
        }
    }

    public class UserBuilder {

        private User user;

        private UserBuilder(String email, String password, String firstName, String lastName) {
            user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
        }

        public User save() {
            log.info("Adding new User {} to database", user);
            return userRepository.save(user);
        }

        public UserBuilder withEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public UserBuilder withPassword(String rawPassword) {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            return this;
        }

        public UserBuilder withRoles(String... roleNames) {
            List<Role> roles = Stream.of(roleNames).map(roleRepository::findByName).collect(toList());
            return this.withRoles(roles.toArray(new Role[]{}));
        }

        public UserBuilder withRoles(Role... roles) {
            user.setRoles(asList(roles));
            return this;
        }

        public UserBuilder withEnabled(boolean enabled) {
            user.setEnabled(enabled);
            return this;
        }

    }

    public class VerificationTokenBuilder {

        private VerificationToken verificationToken;

        private VerificationTokenBuilder(User user) {
            String token = UUID.randomUUID().toString();
            verificationToken = new VerificationToken(token, user);
        }

        public VerificationToken save() {
            log.info("Adding new VerificationToken {} to database", verificationToken);
            return tokenRepository.save(verificationToken);
        }

        public VerificationTokenBuilder withExpiryDate(Date date) {
            verificationToken.setExpiryDate(date);
            return this;
        }

    }

}
