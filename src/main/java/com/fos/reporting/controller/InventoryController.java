package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.domain.InventoryRecordDto;
import com.fos.reporting.domain.ProductInventoryStatusDto; // Import the new DTO
import com.fos.reporting.service.InventoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/inventory") //
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * GET /api/inventory/latest : Gets the latest inventory status for all products.
     *
     * @return A list of products with their most recent inventory levels.
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ProductInventoryStatusDto>> getLatestInventory() {
        List<ProductInventoryStatusDto> latestInventory = inventoryService.getLatestInventoryForAllProducts();
        return ResponseEntity.ok(latestInventory);
    }

    @PostMapping
    public ResponseEntity<InventoryRecordDto> recordTransaction(@Valid @RequestBody InventoryDto inventoryDto) {
        InventoryRecordDto savedRecord = inventoryService.recordInventoryTransaction(inventoryDto, UUID.randomUUID().toString());
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryRecordDto>> getProductHistory(@PathVariable Long productId) {
        List<InventoryRecordDto> history = inventoryService.getHistoryForProduct(productId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/histroty")
    public ResponseEntity<?> getAllInventoryLogs() {
        try {
            log.info("Fetching all inventory logs");
            List<InventoryRecordDto> logs = inventoryService.getAllInventoryLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching all inventory logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch all inventory logs: " + e.getMessage());
        }
    }
}