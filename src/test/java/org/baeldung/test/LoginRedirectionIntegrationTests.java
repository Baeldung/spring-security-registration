package org.baeldung.test;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.baeldung.Application;
import org.baeldung.spring.TestDbConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class})
@AutoConfigureMockMvc
public class LoginRedirectionIntegrationTests {

    @Autowired
    private MockMvc mvc;

	@Test
	public void testManagerLoginRedirection() throws Exception {
		
		mvc.perform(post("/login")
           .param("username", "manager@test.com")
           .param("password", "manager")
           .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
           .andExpect(status().is(302))
           .andExpect(redirectedUrl("/management.html"));
	}
	
	@Test
	public void testNonManagerLoginRedirection() throws Exception {
		
		mvc.perform(post("/login")
           .param("username", "test@test.com")
           .param("password", "test")
           .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
           .andExpect(status().is(302))
           .andExpect(redirectedUrl("/console.html"));
	}

	@Test
	@WithMockUser(authorities = {"MANAGE_PRIVILEGE"})
	public void testSecuredManagementPageOk() throws Exception {
		
	    mvc.perform(get("/management.html"))
	            .andExpect(authenticated())
	            .andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = {"READ_PRIVILEGE", "WRITE_PRIVILEGE", "CHANGE_PASSWORD_PRIVILEGE"})
	public void testSecuredManagementPage403() throws Exception {
		
	    mvc.perform(get("/management.html"))
	            .andExpect(authenticated())
	            .andExpect(status().isForbidden());
	}
}
