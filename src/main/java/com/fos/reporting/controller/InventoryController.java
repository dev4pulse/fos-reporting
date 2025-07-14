package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.domain.UpdatePriceDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.repository.InventoryRepository;
import com.fos.reporting.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    //  Adding to Inventory
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

    //  Latest Price Fetch
    @GetMapping("/inventory/price")
    public ResponseEntity<Float> getPriceFromInventory(@RequestParam String productName) {
        return inventoryRepository
                .findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(productName)
                .map(inventory -> ResponseEntity.ok(inventory.getPrice()))
                .orElse(ResponseEntity.notFound().build());
    }

    //  Inventory Fetch
    @GetMapping("/inventory/latest")
    public ResponseEntity<List<Inventory>> getLatestInventoryPerProduct() {
        List<String> distinctNames = inventoryRepository.findDistinctProductNames();
        List<Inventory> latest = new ArrayList<>();
        for (String name : distinctNames) {
            inventoryRepository
                    .findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(name)
                    .ifPresent(latest::add);
        }
        return ResponseEntity.ok(latest);
    }


    //  Only Price Update
    @PutMapping("/inventory/update-price")
    public ResponseEntity<String> updateProductPrice(@RequestBody @Valid UpdatePriceDto dto) {
        boolean updated = inventoryService.updatePrice(dto.getProductName(), dto.getNewPrice());
        if (updated) {
            return ResponseEntity.ok("Price updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in inventory");
        }
    }

}