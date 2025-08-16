package com.fos.reporting.controller;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.SalesRepository;
import com.fos.reporting.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<String> addEntry(@RequestBody @Valid EntrySaleDto entrySaleDto) {
        try {
            if (reportService.addToSales(entrySaleDto, UUID.randomUUID().toString())) {
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
            if (reportService.addToCollections(collectionsDto, UUID.randomUUID().toString())) {
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

    @GetMapping("/sales")
    public ResponseEntity<List<Sales>> getAllSales() {
        List<Sales> salesList = salesRepository.findAll();
        return ResponseEntity.ok(salesList);
    }

    @GetMapping("/recentSales")
    public ResponseEntity<List<Sales>> getRecentSales() {
        List<Sales> salesList = reportService.getRecentSales();
        return ResponseEntity.ok(salesList);
    }

    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<String> deleteByEntryId(@PathVariable String entryId) {
        try {
            reportService.deleteById(entryId);
            return ResponseEntity.ok("Sale deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete sale");
        }
    }

    @GetMapping("/recent-entries")
    public ResponseEntity<?> getRecentEntries() {
        try {
            List<EntryData> recentEntries = reportService.getRecentEntries();
            return ResponseEntity.ok(recentEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/entryData")
    public ResponseEntity<String> addEntryData(@RequestBody @Valid EntryData entryData) {
        try {
            if (reportService.addData(entryData)) {
                return ResponseEntity.ok("added data to sales collections and inventory");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        }
    }
}