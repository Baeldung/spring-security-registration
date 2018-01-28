package org.baeldung.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.baeldung.Application;
import org.baeldung.common.DatabaseCleaner;
import org.baeldung.common.EntityBootstrap;
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
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChangePasswordIntegrationTest {

    private static final String BASE_ADDRESS = "http://localhost";
    private static final String LOGIN_PATH = "/login";
    private static final String UPDATE_PASSWORD_PATH = "/user/updatePassword";

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "test";
    private static final String CONSOLE_ENDPOINT = "/console.html";

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityBootstrap entityBootstrap;

    @Value("${local.server.port}")
    int port;

    private URI updatePasswordUrl;

    private FormAuthConfig formConfig;

    //

    @Before
    public void init() {
        databaseCleaner.clean();

        entityBootstrap.newUser()
                .withEmail(USER_EMAIL)
                .withPassword(USER_PASSWORD)
                .withEnabled(true)
                .withRoles("ROLE_ADMIN")
                .save();

        RestAssured.port = port;

        URI baseUrl = UriComponentsBuilder.fromHttpUrl(BASE_ADDRESS).port(port).build().toUri();
        String loginUrl = baseUrl.resolve(LOGIN_PATH).toString();
        updatePasswordUrl = baseUrl.resolve(UPDATE_PASSWORD_PATH);
        formConfig = new FormAuthConfig(loginUrl, "username", "password");
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().form(USER_EMAIL, USER_PASSWORD, formConfig);

        Response response = request.when().get(CONSOLE_ENDPOINT);

        assertThat(response.getStatusCode()).isEqualTo(200);

        response.then().assertThat().statusCode(200)
                .body(containsString("home"));
    }

    @Test
    public void givenNotAuthenticatedUser_whenBadPasswordLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().form("XXXXXXXX@XXXXXXXXX.com", "XXXXXXXX", formConfig).redirects().follow(false);

        Response response = request.when().get(CONSOLE_ENDPOINT);

        assertThat(response.getStatusCode()).isNotEqualTo(200);
        assertThat(response.body().asString()).isNullOrEmpty();
    }

    @Test
    public void givenLoggedInUser_whenChangingPassword_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().form(USER_EMAIL, USER_PASSWORD, formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("oldPassword", USER_PASSWORD);
        params.put("newPassword", "newTest&12");

        final Response response = request.with().queryParameters(params).post(updatePasswordUrl);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().asString()).contains("Password updated successfully");
    }

    @Test
    public void givenWrongOldPassword_whenChangingPassword_thenBadRequest() {
        final RequestSpecification request = RestAssured.given().auth().form(USER_EMAIL, USER_PASSWORD, formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("oldPassword", "abc");
        params.put("newPassword", "newTest&12");

        final Response response = request.with().queryParameters(params).post(updatePasswordUrl);

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body().asString()).contains("Invalid Old Password");
    }

    @Test
    public void givenNotAuthenticatedUser_whenChangingPassword_thenRedirect() {
        final Map<String, String> params = new HashMap<>();
        params.put("oldPassword", "abc");
        params.put("newPassword", "xyz");

        final Response response = RestAssured.with().params(params).post(updatePasswordUrl);

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.body().asString()).doesNotContain("Password updated successfully");
    }

}
