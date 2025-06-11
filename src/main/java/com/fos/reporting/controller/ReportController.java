package com.fos.reporting.controller;

import com.fos.reporting.domain.CollectionsDto;
import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.domain.GetReportRequest;
import com.fos.reporting.domain.GetReportResponse;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/test")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("test from report service");
    }

    @PostMapping("/sales")
    public ResponseEntity<String> addEntry(@RequestBody @Validated EntryProduct entryProduct) {
        if (reportService.addToSales(entryProduct)) {
            return ResponseEntity.ok("added to sales");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
    }

    @PostMapping("/collections")
    public ResponseEntity<String> addCollections(@RequestBody @Validated CollectionsDto collectionsDto) {
        if (reportService.addToCollections(collectionsDto)) {
            return ResponseEntity.ok("added to collections");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
    }

    @PostMapping("/dashboard-data")
    public ResponseEntity<GetReportResponse> getDashboardData(@RequestBody @Validated GetReportRequest req) {
        return ResponseEntity.ok(reportService.getDashboard(req));
    }

    /**
     * GET /sales/last?productName=...&subProduct=...
     * Returns the most recent closingStock for that product+sub-product.
     */
    @GetMapping("/sales/last")
    public ResponseEntity<Map<String, Float>> getLastClosing(
            @RequestParam String productName,
            @RequestParam String subProduct
    ) {
        Sales last = reportService.findLastSale(productName, subProduct);
        float closing = (last != null) ? last.getClosingStock() : 0f;
        return ResponseEntity.ok(Map.of("lastClosing", closing));
    }
}
