package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;  // ← import for List

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // GET /inventory
    @GetMapping
    public ResponseEntity<List<InventoryDto>> listAll() {
        List<InventoryDto> all = inventoryService.findAll();
        return ResponseEntity.ok(all);
    }

    // POST /inventory
    @PostMapping
    public ResponseEntity<String> addEntry(@RequestBody @Validated InventoryDto inventoryDto) {
        boolean ok = inventoryService.addToInventory(inventoryDto);
        return ok
            ? ResponseEntity.ok("added to inventory")
            : ResponseEntity.status(500).body("failed to add to inventory");
    }
}
