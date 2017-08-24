package org.baeldung.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class RegistrationPasswordLiveTest {
    private final String BASE_URI = "http://localhost:8081/";

    @Test
    public void givenInvalidPassword_thenBadRequest() {
        // too short
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123"));

        // no special character
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1abZRplYU"));

        // no upper case letter
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abidpsvl"));

        // no number
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("abZRYUpl"));

        // alphabet sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abcZRYU"));

        // qwerty sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abZRTYU"));

        // numeric sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123_zqrtU"));

        // valid password
        assertEquals(HttpStatus.OK.value(), getResponseForPassword("12_zwRHIPKA"));
    }

    private int getResponseForPassword(String pass) {
        final Map<String, String> param = new HashMap<String, String>();
        final String randomName = UUID.randomUUID()
            .toString();
        param.put("firstName", randomName);
        param.put("lastName", "Doe");
        param.put("email", randomName + "@x.com");
        param.put("password", pass);
        param.put("matchingPassword", pass);

        final Response response = RestAssured.given()
            .formParameters(param)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .post(BASE_URI + "user/registration");
        System.out.println(response.asString());
        return response.getStatusCode();
    }
}
