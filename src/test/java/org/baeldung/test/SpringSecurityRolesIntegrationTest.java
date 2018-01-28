package org.baeldung.test;

import org.baeldung.common.DatabaseCleaner;
import org.baeldung.common.EntityBootstrap;
import org.baeldung.persistence.dao.PrivilegeRepository;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.spring.TestDbConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDbConfig.class)
@Transactional
public class SpringSecurityRolesIntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityBootstrap entityBootstrap;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    // tests

    @Test
    public void testDeleteUser() {
        databaseCleaner.clean();

        Role role = entityBootstrap.newRole().withName("TEST_ROLE").save();
        User user = entityBootstrap.newUser()
                .withEmail("john@doe.com")
                .withPassword("123")
                .withRoles(role)
                .withEnabled(true)
                .save();

        assertThat(userRepository.findByEmail(user.getEmail())).isNotNull();
        assertThat(roleRepository.findByName(role.getName())).isNotNull();

        user.setRoles(null);
        userRepository.delete(user);

        assertThat(userRepository.findByEmail(user.getEmail())).isNull();
        assertThat(roleRepository.findByName(role.getName())).isNotNull();
    }

    @Test
    public void testDeleteRole() {
        Privilege privilege = entityBootstrap.newPrivilege().withName("TEST_PRIVILEGE").save();
        Role role = entityBootstrap.newRole().withName("TEST_ROLE").withPrivileges(privilege).save();
        User user = entityBootstrap.newUser()
                .withEmail("john@doe.com")
                .withPassword("123")
                .withRoles(role)
                .withEnabled(true)
                .save();

        assertThat(userRepository.findByEmail(user.getEmail())).isNotNull();
        assertThat(roleRepository.findByName(role.getName())).isNotNull();
        assertThat(privilegeRepository.findByName(privilege.getName())).isNotNull();

        user.setRoles(new ArrayList<>());
        role.setPrivileges(new ArrayList<>());
        roleRepository.delete(role);

        assertThat(userRepository.findByEmail(user.getEmail())).isNotNull();
        assertThat(roleRepository.findByName(role.getName())).isNull();
        assertThat(privilegeRepository.findByName(privilege.getName())).isNotNull();
    }

    @Test
    public void testDeletePrivilege() {
        Privilege privilege = entityBootstrap.newPrivilege().withName("TEST_PRIVILEGE").save();
        Role role = entityBootstrap.newRole().withName("TEST_ROLE").withPrivileges(privilege).save();

        assertThat(roleRepository.findByName(role.getName())).isNotNull();
        assertThat(privilegeRepository.findByName(privilege.getName())).isNotNull();

        role.setPrivileges(new ArrayList<>());
        privilegeRepository.delete(privilege);

        assertThat(roleRepository.findByName(role.getName())).isNotNull();
        assertThat(privilegeRepository.findByName(privilege.getName())).isNull();
    }

}
