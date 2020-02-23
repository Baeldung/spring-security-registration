package org.baeldung.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RunWith(MockitoJUnitRunner.class)
public class MySimpleUrlAuthenticationSuccessHandlerTest {

    private MySimpleUrlAuthenticationSuccessHandler handler;
    private Authentication authentication;
    private Set<SimpleGrantedAuthority> grantedAuthority;

    @Before
    public void init() {
        handler = new MySimpleUrlAuthenticationSuccessHandler();
        authentication = mock(Authentication.class);
        grantedAuthority = new HashSet<>();
        doReturn(grantedAuthority).when(authentication).getAuthorities();
    }

    @Test
    public void givenUpdateExpectManagementPage() {
        //Arrange
        grantedAuthority.add(new SimpleGrantedAuthority("UPDATE_PRIVILEGE"));

        //Act
        String result = handler.determineTargetUrl(authentication);

        //Assert
        assertEquals("/management.html", result);
    }

    @Test
    public void givenReadExpectHomePage(){
        //Arrange
        grantedAuthority.add(new SimpleGrantedAuthority("READ_PRIVILEGE"));
        doReturn("test").when(authentication).getName();
        //Act
        String result = handler.determineTargetUrl(authentication);

        //Assert
        assertEquals("/homepage.html?user=test", result);
    }

    @Test
    public void givenReadWriteExpectConsolePage(){
        //Arrange
        grantedAuthority.add(new SimpleGrantedAuthority("READ_PRIVILEGE"));
        grantedAuthority.add(new SimpleGrantedAuthority("WRITE_PRIVILEGE"));

        //Act
        String result = handler.determineTargetUrl(authentication);

        //Assert
        assertEquals("/console.html", result);
    }
}