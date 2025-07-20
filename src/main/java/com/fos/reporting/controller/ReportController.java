package com.fos.reporting.controller;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.SalesRepository;
import com.fos.reporting.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private SalesRepository salesRepository;

    @GetMapping("/test")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("test from report service");
    }

    @PostMapping("/sales")
    public ResponseEntity<String> addEntry(@RequestBody @Valid EntryProduct entryProduct) {
        try {
            if (reportService.addToSales(entryProduct)) {
                return ResponseEntity.ok("added to sales");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        }
    }

    @GetMapping("/sales/last")
    public ResponseEntity<?> getLastClosing(@RequestParam String productName, @RequestParam String gun) {
        try {
            float last = reportService.getLastClosing(productName, gun);
            return ResponseEntity.ok(Map.of("lastClosing", last));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

    @PostMapping("/collections")
    public ResponseEntity<String> addCollections(@RequestBody @Valid CollectionsDto collectionsDto) {
        try {
            if (reportService.addToCollections(collectionsDto)) {
                return ResponseEntity.ok("added to collections");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        }
    }

    @PostMapping("/dashboard-data")
    public ResponseEntity<GetReportResponse> getDashboardData(@RequestBody @Valid GetReportRequest getReportRequest) {
        try {
            return ResponseEntity.ok(reportService.getDashboard(getReportRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/price")
    public ResponseEntity<Float> getProductPrice(@RequestParam String productName, @RequestParam String gun) {
        Sales last = salesRepository.findTopByProductNameAndGunOrderByDateTimeDesc(productName, gun);
        return ResponseEntity.ok((last != null) ? last.getPrice() : 0f);
    }
}