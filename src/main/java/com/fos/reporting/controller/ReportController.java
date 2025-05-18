package com.fos.reporting.controller;

import com.fos.reporting.domain.CollectionsDto;
import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

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

    @PostMapping("/sales")
    public ResponseEntity<String> addEntry(@RequestBody @Validated EntryProduct entryProduct) {
        try {
            if (reportService.addToSales(entryProduct)) {
                return new ResponseEntity<>("added to sales", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/collections")
    public ResponseEntity<String> addCollections(@RequestBody @Validated CollectionsDto collectionsDto) {
        try {
            if (reportService.addToCollections(collectionsDto)) {
                return new ResponseEntity<>("added to collections", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
