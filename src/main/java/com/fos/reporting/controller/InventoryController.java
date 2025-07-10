package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

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
}