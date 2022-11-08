package com.baeldung.test;

import static com.baeldung.util.AppConstants.MANAGE_PRIVILEGE;
import static com.baeldung.util.AppConstants.ROLE_MANAGER;
import static com.baeldung.util.AppConstants.READ_PRIVILEGE;
import static com.baeldung.util.AppConstants.ROLE_USER;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
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
@SpringBootTest(classes = { Application.class, TestDbConfig.class,
		TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ManagerRoleIntegrationTest {

	@Value("${local.server.port}")
	int port;

	private FormAuthConfig formConfig;

	@BeforeEach
	public void init() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
		formConfig = new FormAuthConfig("/login", "username", "password");
	}

	@Test
	public void testManagementUrlByManagerUser() {

		final RequestSpecification request = RestAssured.given().auth().form("manager@test.com", "m", formConfig);

		final Response response = request.with().log().all().get("/management");
		assertEquals(200, response.statusCode());
		assertTrue(response.body().asString().contains("Management Home"));
	}

	@Test
	public void testManagementUrlByNonManagerUser() {
		final RequestSpecification request = RestAssured.given().auth().form("user@test.com", "u", formConfig);
		final Response response = request.with().log().all().get("/management");
		assertEquals(200, response.statusCode());
		assertTrue(response.body().asString().contains("Access denied, please go back to home page"));
	}
}
