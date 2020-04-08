package com.baeldung.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ // @formatter:off 
    ChangePasswordIntegrationTest.class, 
    TokenExpirationIntegrationTest.class,
    RegistrationControllerIntegrationTest.class,
    GetLoggedUsersIntegrationTest.class,
    UserServiceIntegrationTest.class,
    UserIntegrationTest.class,
    SpringSecurityRolesIntegrationTest.class,
    LocalizationIntegrationTest.class
})// @formatter:on
public class IntegrationSuite {
    //
}