package com.baeldung.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ManagementController {
    
    @GetMapping("/management")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ModelAndView management(final ModelMap model) {
        return new ModelAndView("management", model);
    }
}
