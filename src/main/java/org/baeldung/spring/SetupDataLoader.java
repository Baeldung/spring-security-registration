package org.baeldung.spring;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.baeldung.persistence.dao.PrivilegeRepository;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");
        final Privilege managerPrivilege = createPrivilegeIfNotFound("MANAGER");

        // == create initial roles
        final List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege);
        final List<Privilege> userPrivileges = Arrays.asList(readPrivilege, passwordPrivilege);
        final List<Privilege> managerPrivilidges = Arrays.asList(readPrivilege, passwordPrivilege, managerPrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);
        createRoleIfNotFound("ROLE_MANAGER", managerPrivilidges);

        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        final User admin = new User();
        admin.setFirstName("Test");
        admin.setLastName("Test");
        admin.setPassword(passwordEncoder.encode("test"));
        admin.setEmail("test@test.com");
        admin.setRoles(Arrays.asList(adminRole));
        admin.setEnabled(true);
        userRepository.save(admin);

        final Role managerRole = roleRepository.findByName("ROLE_MANAGER");
        final User manager = new User();
        manager.setFirstName("Michal");
        manager.setLastName("Menago");
        manager.setPassword(passwordEncoder.encode("menago"));
        manager.setEmail("michal@menago.com");
        manager.setRoles(Arrays.asList(managerRole));
        manager.setEnabled(true);
        userRepository.save(manager);

        alreadySetup = true;
    }

    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

}