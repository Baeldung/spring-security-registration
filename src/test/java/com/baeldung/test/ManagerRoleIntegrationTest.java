package com.baeldung.test;

import static com.baeldung.util.AppConstants.MANAGE_PRIVILEGE;
import static com.baeldung.util.AppConstants.ROLE_MANAGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baeldung.Application;
import com.baeldung.persistence.dao.PrivilegeRepository;
import com.baeldung.persistence.dao.RoleRepository;
import com.baeldung.persistence.dao.UserRepository;
import com.baeldung.persistence.model.Privilege;
import com.baeldung.persistence.model.Role;
import com.baeldung.persistence.model.User;
import com.baeldung.spring.TestDbConfig;
import com.baeldung.spring.TestIntegrationConfig;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ManagerRoleIntegrationTest {

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${local.server.port}")
    int port;

    private User user;
    private Role role;
    private Privilege privilege;
    private FormAuthConfig formConfig;
    
    @BeforeEach
    public void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        formConfig = new FormAuthConfig("/login", "username", "password");
    }
	
	@Test
	public void testManagementUrlByManagerUser() {
		
		// Create user with managerial role and privileges
		privilege = new Privilege(MANAGE_PRIVILEGE);
        privilegeRepository.save(privilege);

        role = new Role(ROLE_MANAGER);
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
        
        final RequestSpecification request = RestAssured.given().auth().form("john@doe.com", "123", formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("password", "test");

        final Response response = request.with().params(params).get("/management");
        assertEquals(200, response.statusCode());
	}
	
	@Test
	public void testManagementUrlByNonManagerUser() {
		
		// Create user with managerial role and privileges
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
        
        final RequestSpecification request = RestAssured.given().auth().form("john@doe.com", "123", formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("password", "test");

        final Response response = request.with().params(params).get("/management");
        assertEquals(302, response.statusCode());
	}
}
