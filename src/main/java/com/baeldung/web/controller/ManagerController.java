package com.baeldung.web.controller;

import com.baeldung.persistence.model.User;
import com.baeldung.security.ActiveUserStore;
import com.baeldung.service.IManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ManagerController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    ActiveUserStore activeUserStore;
    @Autowired
    IManagerService managerService;

    @GetMapping("/management")
    public String getAllManagers() {
        User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<String> managersView =  managerService.getAllManagers(loggedInUser);
        if(managersView.isPresent()) {
            return managersView.get();
        } else {
            logger.info("User {} is not authorized to access manager page",loggedInUser.getFirstName());
            return "accessDenied";
        }
    }
}
