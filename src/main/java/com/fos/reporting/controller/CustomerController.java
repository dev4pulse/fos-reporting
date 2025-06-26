package com.fos.reporting.controller;

import com.fos.reporting.domain.CustomerDto;
import com.fos.reporting.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ── 1) Serve the HTML page via Thymeleaf ──
    @GetMapping(produces = "text/html")
    public String customerPage() {
        return "customer";       // resolves to templates/customer.html
    }

    // ── 2) Return JSON list for AJAX ──
    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<CustomerDto> listAll() {
        return customerService.findAll();
    }

    // ── 3) Accept POST from the form ──
    @PostMapping
    @ResponseBody
    public ResponseEntity<String> addCustomer(
            @RequestBody @Validated CustomerDto customerDto
    ) {
        boolean saved = customerService.addCustomer(customerDto);
        if (saved) {
            return ResponseEntity.ok("Customer added successfully");
        }
        return ResponseEntity.status(500).body("Failed to add customer");
    }
}
