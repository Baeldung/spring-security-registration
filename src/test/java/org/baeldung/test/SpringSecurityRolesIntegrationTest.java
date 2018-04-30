package org.baeldung.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.specification.RequestSpecification;
import org.baeldung.Application;
import org.baeldung.persistence.dao.PrivilegeRepository;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class SpringSecurityRolesIntegrationTest {

    private final String USER_MANAGER_EMAIL = "test.manager@test.com";
    private final String MANAGER_ROLE_NAME = "ROLE_MANAGER";

    @Value("${local.server.port}")
    int port;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Role role;
    private Privilege privilege;
    private FormAuthConfig formConfig;

    // tests


    @Before
    public void init() throws Exception {
        RestAssured.port = port;
        final String URL_PREFIX = "http://localhost:" + String.valueOf(port);
        createUserManagerIfNotExist(USER_MANAGER_EMAIL);
        formConfig = new FormAuthConfig(URL_PREFIX + "/login", "username", "password");
    }

    @After
    public void tearDown() throws Exception {
        userRepository = null;
        roleRepository = null;
        privilegeRepository = null;
        passwordEncoder = null;
        user = null;
        role = null;
        privilege = null;
        formConfig = null;
    }

    @Test
    public void testDeleteUser() {
        role = new Role("TEST_ROLE");
        roleRepository.save(role);

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEmail("john@doe.com");
        user.setRoles(Arrays.asList(role));
        user.setEnabled(true);
        userRepository.save(user);

        assertNotNull(userRepository.findByEmail(user.getEmail()));
        assertNotNull(roleRepository.findByName(role.getName()));
        user.setRoles(null);
        userRepository.delete(user);

        assertNull(userRepository.findByEmail(user.getEmail()));
        assertNotNull(roleRepository.findByName(role.getName()));
    }

    @Test
    public void testDeleteRole() {
        privilege = new Privilege("TEST_PRIVILEGE");
        privilegeRepository.save(privilege);

        role = new Role("TEST_ROLE");
        role.setPrivileges(Arrays.asList(privilege));
        roleRepository.save(role);

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEmail("john@doe.com");
        user.setRoles(Arrays.asList(role));
        user.setEnabled(true);
        userRepository.save(user);

        assertNotNull(privilegeRepository.findByName(privilege.getName()));
        assertNotNull(userRepository.findByEmail(user.getEmail()));
        assertNotNull(roleRepository.findByName(role.getName()));

        user.setRoles(new ArrayList<>());
        role.setPrivileges(new ArrayList<>());
        roleRepository.delete(role);

        assertNull(roleRepository.findByName(role.getName()));
        assertNotNull(privilegeRepository.findByName(privilege.getName()));
        assertNotNull(userRepository.findByEmail(user.getEmail()));
    }

    @Test
    public void testDeletePrivilege() {
        privilege = new Privilege("TEST_PRIVILEGE");
        privilegeRepository.save(privilege);

        role = new Role("TEST_ROLE");
        role.setPrivileges(Arrays.asList(privilege));
        roleRepository.save(role);

        assertNotNull(roleRepository.findByName(role.getName()));
        assertNotNull(privilegeRepository.findByName(privilege.getName()));

        role.setPrivileges(new ArrayList<>());
        privilegeRepository.delete(privilege);

        assertNull(privilegeRepository.findByName(privilege.getName()));
        assertNotNull(roleRepository.findByName(role.getName()));
    }

    @Test
    public void testRoleManagerExist() {
        User userManager = userRepository.findByEmail(USER_MANAGER_EMAIL);
        assertNotNull(userManager);
        assertEquals(MANAGER_ROLE_NAME, userManager.getRoles().iterator().next().getName());
    }

    @Test
    public void testLoginUserWithManagerRole() {
        final RequestSpecification request = RestAssured.given().auth().form(USER_MANAGER_EMAIL, "test", formConfig);

        request.when().get("/manager.html").then().assertThat().statusCode(200);
    }

    private Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    private Role createManagerRoleIfNotExist(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
            Privilege managePrivilege = createPrivilegeIfNotFound("MANAGE_PRIVILEGE");
            List<Privilege> managerPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, managePrivilege));
            role.setPrivileges(managerPrivileges);
            role = roleRepository.save(role);
        }

        return role;
    }

    private void createUserManagerIfNotExist(String managerEmail) {
        User managerUser = userRepository.findByEmail(managerEmail);
        if (managerUser == null) {
            Role managerRole = createManagerRoleIfNotExist(MANAGER_ROLE_NAME);
            managerUser = new User();
            managerUser.setFirstName("Test");
            managerUser.setLastName("Test");
            managerUser.setPassword(passwordEncoder.encode("test"));
            managerUser.setEmail(managerEmail);
            managerUser.setEnabled(true);
            managerUser.setRoles(new ArrayList<>(Arrays.asList(managerRole)));
            userRepository.save(managerUser);
        }
    }
}
