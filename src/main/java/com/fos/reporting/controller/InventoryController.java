package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.domain.InventoryRecordDto;
import com.fos.reporting.domain.ProductInventoryStatusDto; // Import the new DTO
import com.fos.reporting.entity.InventoryLog;
import com.fos.reporting.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inventory") // ✅ Standardized path
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
        InventoryRecordDto savedRecord = inventoryService.recordInventoryTransaction(inventoryDto);
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryRecordDto>> getProductHistory(@PathVariable Long productId) {
        List<InventoryRecordDto> history = inventoryService.getHistoryForProduct(productId);
        return ResponseEntity.ok(history);
    }
}