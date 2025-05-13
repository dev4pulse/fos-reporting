package com.fos.reporting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @GetMapping("/test")
    public ResponseEntity<String> ping() {
        try {
            System.out.println("report service call");
            return new ResponseEntity<>("test from report service", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
