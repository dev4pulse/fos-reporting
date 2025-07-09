package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/inventory")
    public ResponseEntity<String> addEntry(@RequestBody @Validated InventoryDto inventoryDto ) {
        try {
            if (inventoryService.addToInventory(inventoryDto)) {
                return new ResponseEntity<>("added to inventory", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}