package com.fos.reporting.controller;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.SalesRepository;
import com.fos.reporting.service.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sales")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private SalesRepository salesRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/test")
    public ResponseEntity<String> ping() {
        log.info("Ping request received");
        return ResponseEntity.ok("test from report service");
    }

    @PostMapping("/sales")
    public ResponseEntity<String> addEntry(@RequestBody @Valid EntrySaleDto entrySaleDto) {
        log.info("Add Sale request received: {}", entrySaleDto);
        try {
            boolean added = reportService.addToSales(entrySaleDto, UUID.randomUUID().toString());
            if (added) {
                log.info("Sale added successfully: {}", entrySaleDto);
                return ResponseEntity.ok("added to sales");
            } else {
                log.error("Failed to add sale: {}", entrySaleDto);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add sale");
            }
        } catch (Exception e) {
            log.error("Exception in addEntry: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add sale due to exception");
        }
    }

    @GetMapping("/sales/last")
    public ResponseEntity<?> getLastClosing(@RequestParam String productName, @RequestParam String gun) {
        log.info("Get last closing request for product: {} and gun: {}", productName, gun);
        try {
            float last = reportService.getLastClosing(productName, gun);
            return ResponseEntity.ok(Map.of("lastClosing", last));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting last closing: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }

    @PostMapping("/collections")
    public ResponseEntity<String> addCollections(@RequestBody @Valid CollectionsDto collectionsDto) {
        log.info("Add collections request: {}", collectionsDto);
        try {
            boolean added = reportService.addToCollections(collectionsDto, UUID.randomUUID().toString());
            if (added) {
                log.info("Collections added successfully: {}", collectionsDto);
                return ResponseEntity.ok("added to collections");
            } else {
                log.error("Failed to add collections: {}", collectionsDto);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add collections");
            }
        } catch (Exception e) {
            log.error("Exception in addCollections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add collections due to exception");
        }
    }

    @PostMapping("/dashboard-data")
    public ResponseEntity<GetReportResponse> getDashboardData(@RequestBody @Valid GetReportRequest getReportRequest) {
        log.info("Dashboard data request received: {}", getReportRequest);
        try {
            GetReportResponse response = reportService.getDashboard(getReportRequest);
            log.info("Dashboard data retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Exception in getDashboardData: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/price")
    public ResponseEntity<Float> getProductPrice(@RequestParam String productName, @RequestParam String gun) {
        log.info("Get product price request for product: {} gun: {}", productName, gun);
        try {
            Sales last = salesRepository.findTopByProductNameAndGunOrderByDateTimeDesc(productName, gun);
            float price = (last != null) ? last.getPrice() : 0f;
            log.info("Product price: {}", price);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            log.error("Error fetching product price: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<Sales>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get all sales request with page: {} size: {}", page, size);
        try {
            Page<Sales> salesPage = reportService.getAllSales(page, size);
            return ResponseEntity.ok(salesPage);
        } catch (Exception e) {
            log.error("Error fetching all sales: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Sales>> getSalesByDateRangeAndProduct(
            @NotNull @RequestParam String fromDate,
            @NotNull @RequestParam String toDate,
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Get sales by date range request: from {} to {}, product: {}", fromDate, toDate, productName);
        try {
            LocalDateTime from = LocalDateTime.parse(fromDate, FORMATTER);
            LocalDateTime to = LocalDateTime.parse(toDate, FORMATTER);
            Page<Sales> salesPage;

            if (productName != null && !productName.isEmpty()) {
                salesPage = reportService.getSalesByDateRangeAndProduct(from, to, productName, page, size);
                log.info("Fetched {} sales for product {}", salesPage.getNumberOfElements(), productName);
            } else {
                salesPage = reportService.getSalesByDateRange(from, to, page, size);
                log.info("Fetched {} sales in date range", salesPage.getNumberOfElements());
            }

            return ResponseEntity.ok(salesPage);
        } catch (Exception e) {
            log.error("Error fetching sales by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
