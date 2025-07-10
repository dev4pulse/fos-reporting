package com.fos.reporting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
    @GetMapping("/sales-form")
    public String showSalesForm() {
        return "sales-form";
    }

    @GetMapping("/entry")
    public String addEntry() {
        return "entry";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    @GetMapping("/collections-form")
    public String showCollectionsForm() {
        return "collections-form";
    }
}
