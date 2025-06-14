package com.fos.reporting.controller;

import com.fos.reporting.domain.*;           // wildcard import for CollectionsDto, EntryProduct, etc.
import com.fos.reporting.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    // === Unchanged: simple health-check endpoint ===
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

    // === New: fetch prior closing-stock for a given product & subProduct ===
    @GetMapping("/sales/last")
    public ResponseEntity<Map<String, Float>> getLastClosing(
            @RequestParam String productName,
            @RequestParam String subProduct
    ) {
        float last = reportService.getLastClosing(productName, subProduct);
        return ResponseEntity.ok(Map.of("lastClosing", last));
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

    @PostMapping("/dashboard-data")
    public ResponseEntity<GetReportResponse> getDashboardData(@RequestBody @Validated GetReportRequest getReportRequest) {
        try {
            return new ResponseEntity<>(reportService.getDashboard(getReportRequest), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

