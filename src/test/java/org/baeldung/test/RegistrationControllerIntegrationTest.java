package org.baeldung.test;

import org.baeldung.Application;
import org.baeldung.common.EntityBootstrap;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegistrationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EntityBootstrap entityBootstrap;

    private MockMvc mockMvc;
    private String token;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = entityBootstrap.newUser().save();
        VerificationToken verificationToken = entityBootstrap.newVerificationToken(user)
                .withExpiryDate(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)))
                .save();
        token = verificationToken.getToken();
    }

    @Test
    public void testRegistrationConfirm() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm.html?token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(model().attribute("message", "Your account verified successfully"));
        resultActions.andExpect(view().name("redirect:/console.html?lang=en"));
    }

    @Test
    public void testRegistrationValidation() throws Exception {

        final MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("firstName", "");
        param.add("lastName", "");
        param.add("email", "");
        param.add("password", "");
        param.add("matchingPassword", "");

        ResultActions resultActions = this.mockMvc.perform(post("/user/registration").params(param));
        resultActions.andExpect(status().is(400));
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error", is("InvaliduserDto")))
                .andExpect(jsonPath("$.message", containsString("{\"field\":\"lastName\",\"defaultMessage\":\"Length must be greater than 1\"}")));
    }
}