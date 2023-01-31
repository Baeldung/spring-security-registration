package com.baeldung.test;

import static com.baeldung.security.LoginAttemptService.MAX_ATTEMPT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baeldung.Application;
import com.baeldung.persistence.dao.UserRepository;
import com.baeldung.persistence.model.User;
import com.baeldung.spring.TestDbConfig;
import com.baeldung.spring.TestIntegrationConfig;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.specification.RequestSpecification;

/**
 * Test class for the case to see that the user is blocked after several tries
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LockAccountAfterSeveralTriesIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;

    @BeforeEach
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        formConfig = new FormAuthConfig("/login", "username", "password");
    }

    @Test
    public void givenLoggedInUser_whenUsernameOrPasswordIsIncorrectAfterMaxAttempt_thenUserBlockFor24Hours() {
        //first request where a user tries several incorrect credential
        for (int i = 0; i < MAX_ATTEMPT - 2; i++) {
            final RequestSpecification requestIncorrect = RestAssured.given().auth().form("testtesefsdt.com" + i, "tesfsdft", formConfig);

            requestIncorrect.when().get("/console").then().assertThat().statusCode(200).and().body(not(containsString("home")));
        }

        //then user tries a correct user
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        request.when().get("/console").then().assertThat().statusCode(200).and().body(containsString("home"));

        for (int i = 0; i < 3; i++) {
            final RequestSpecification requestSecond = RestAssured.given().auth().form("testtesefsdt.com", "tesfsdft", formConfig);

            requestSecond.when().get("/console").then().assertThat().statusCode(200).and().body(not(containsString("home")));
        }

        //the third request where we can see that the user is blocked even if he previously entered a correct credential
        final RequestSpecification requestCorrect = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        requestCorrect.when().get("/console").then().assertThat().statusCode(200).and().body(not(containsString("home")));
    }
}