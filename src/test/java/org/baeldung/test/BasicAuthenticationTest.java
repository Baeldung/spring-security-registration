package org.baeldung.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.servlet.http.HttpSession;
import org.baeldung.Application;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class BasicAuthenticationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  
  @Autowired
  private FilterChainProxy springSecurityFilter;
  
  private MockMvc mockMvc;
  
  @Before
  public void setUp() {
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*")
          .build();
  }
  
    @Test
    public void whenLoggedIn_thenRedirect() throws Exception {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
      params.add("username", "manager@test.com");
      params.add("password", "manager");
      params.add("code", "");
      params.add("submit", "Submit");
      
      HttpSession session = mockMvc.perform(post("http://localhost:8081/login").params(params))
                      .andExpect(status().is(HttpStatus.FOUND.value()))
                      .andExpect(redirectedUrl("/manager.html")).andReturn().getRequest().getSession();
                      

       Assert.assertNotNull(session);
       
       
    }



}