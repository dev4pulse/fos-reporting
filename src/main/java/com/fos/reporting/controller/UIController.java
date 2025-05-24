package com.fos.reporting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {


    @GetMapping("/sales-form")
    public String showSalesForm() {
        return "sales-form";  // refers to templates/sales-form.html
    }
    @GetMapping("/dashboard")
    public String showDashboardForm() {
        return "dashboard"; // loads dashboard-form.html
    }

    @GetMapping("/collections-form")
    public String showCollectionsForm() {
        return "collections-form"; // maps to collections-form.html
    }

}
