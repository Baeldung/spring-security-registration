package org.baeldung.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.baeldung.Application;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class AuthenticationSuccessRedirectIntegrationTest {

    private static final String BASE_URL = "http://localhost";
    private static final String MANAGEMENT_PATH = "/management.html";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "test";
    private static final String USER_FIRST_NAME = "Test";
    private static final String USER_LAST_NAME = "Test";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    private URI loginUri;

    @Before
    public void setUp() {
        User user = userRepository.findByEmail(USER_EMAIL);
        if (user == null) {
            user = new User();
            user.setFirstName(USER_FIRST_NAME);
            user.setLastName(USER_LAST_NAME);
            user.setPassword(passwordEncoder.encode(USER_PASSWORD));
            user.setEmail(USER_EMAIL);
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode(USER_PASSWORD));
            userRepository.save(user);
        }

        RestAssured.port = port;
        URI host = UriComponentsBuilder.fromHttpUrl(BASE_URL).port(port).build().toUri();
        loginUri = host.resolve(LOGIN_ENDPOINT);
    }

    @Test
    public void shouldRedirectManagerToManagementPage() {
        RequestSpecification request = RestAssured.given()
                .formParam("username", USER_EMAIL)
                .formParam("password", USER_PASSWORD);

        Response response = request.post(loginUri);

        URI redirectUri = URI.create(response.getHeader("Location"));

        assertThat(redirectUri).hasPath(MANAGEMENT_PATH);
    }

}
