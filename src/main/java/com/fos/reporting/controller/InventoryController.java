package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.repository.InventoryRepository;
import com.fos.reporting.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping("/inventory")
    public ResponseEntity<String> addEntry(@RequestBody @Valid InventoryDto inventoryDto) {
        try {
            if (inventoryService.addToInventory(inventoryDto)) {
                return ResponseEntity.ok("added to inventory");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed exception");
        }
    }

    @GetMapping("/inventory/price")
    public ResponseEntity<Float> getPriceFromInventory(@RequestParam String productName) {
        return inventoryRepository
                .findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(productName)
                .map(inventory -> ResponseEntity.ok(inventory.getPrice()))
                .orElse(ResponseEntity.notFound().build());
    }

}