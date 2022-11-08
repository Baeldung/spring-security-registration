package com.baeldung.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({ // @formatter:off
    ChangePasswordIntegrationTest.class,
    DeviceServiceIntegrationTest.class,
    TokenExpirationIntegrationTest.class,
    RegistrationControllerIntegrationTest.class,
    GetLoggedUsersIntegrationTest.class,
    UserServiceIntegrationTest.class,
    UserIntegrationTest.class,
    SpringSecurityRolesIntegrationTest.class,
    LocalizationIntegrationTest.class,
    ManagerRoleIntegrationTest.class
})// @formatter:on
public class IntegrationSuite {
  //
}
