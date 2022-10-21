package com.baeldung.test;

import com.baeldung.persistence.model.Role;
import com.baeldung.persistence.model.User;
import com.baeldung.service.IManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ManagerServiceIntegrationTest {
    @Autowired
    private IManagerService managerService;

    @Test
    public void should_getManagersPage_whenProperManagerRolePersonRequests() {
        User user = new User();
        user.setFirstName("testManager");
        List<Role> roles = new ArrayList<>();
        Role role = new Role("ROLE_MANAGER");
        roles.add(role);
        user.setRoles(roles);
        Optional<String> result = managerService.getAllManagers(user);
        assertEquals(result.get(), "management");
    }

    @Test
    public void should_not_getManagersPage_whenProperManagerRolePersonRequests() {
        User user = new User();
        user.setFirstName("testManager");
        List<Role> roles = new ArrayList<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);
        Optional<String> result = managerService.getAllManagers(user);
        assertEquals( Optional.empty(),result);
    }
}
