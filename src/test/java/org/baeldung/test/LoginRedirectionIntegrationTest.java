package org.baeldung.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginRedirectionIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;


    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void givenLoggedInUser_whenUsingManagerRoles_thenUserIsRedirectedToManagementUI() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "manager@test.com")
                .param("password", "manager");
        ResultActions result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/management.html"));

    }

    @Test
    public void givenLoggedInUser_whenUsingAdminRoles_thenUserIsRedirectedToAdminUI() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "test@test.com")
                .param("password", "test");
        ResultActions result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/console.html"));

    }

}
