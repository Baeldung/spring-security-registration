package org.baeldung.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.baeldung.Application;
import org.baeldung.common.DatabaseCleaner;
import org.baeldung.common.EntityBootstrap;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetLoggedUsersIntegrationTest {

    private static final String BASE_ADDRESS = "http://localhost";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGGED_USERS_PATH = "/loggedUsers";
    private static final String SESSION_REGISTRY_LOGGED_USERS_PATH = "/loggedUsersFromSessionRegistry";
    private static final String USER_PASSWORD = "test";

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityBootstrap entityBootstrap;

    @Value("${local.server.port}")
    private int port;

    private URI baseUrl;

    private FormAuthConfig formConfig;

    private User user;

    //

    @Before
    public void init() {
        databaseCleaner.clean();

        user = entityBootstrap.newUser()
                .withPassword(USER_PASSWORD)
                .withRoles("ROLE_ADMIN")
                .withEnabled(true)
                .save();

        RestAssured.port = port;
        baseUrl = UriComponentsBuilder.fromHttpUrl(BASE_ADDRESS).port(port).build().toUri();
        String loginUrl = baseUrl.resolve(LOGIN_PATH).toString();
        formConfig = new FormAuthConfig(loginUrl, "username", "password");
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromActiveUserStore_thenResponseContainsUser() {
        URI loggedUsersUrl = baseUrl.resolve(LOGGED_USERS_PATH);
        final RequestSpecification request = RestAssured.given().auth().form(user.getEmail(), USER_PASSWORD, formConfig);

        final Response response = request.with().params(singletonMap("password", USER_PASSWORD)).get(loggedUsersUrl);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().asString()).contains(user.getEmail());
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromSessionRegistry_thenResponseContainsUser() {
        URI sessionRegistryLoggedUsersUrl = baseUrl.resolve(SESSION_REGISTRY_LOGGED_USERS_PATH);
        final RequestSpecification request = RestAssured.given().auth().form(user.getEmail(), USER_PASSWORD, formConfig);

        final Response response = request.with().params(singletonMap("password", USER_PASSWORD)).get(sessionRegistryLoggedUsersUrl);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().asString()).contains(user.getEmail());
    }

}
