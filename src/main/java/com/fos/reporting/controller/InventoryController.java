package com.fos.reporting.controller;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.domain.UpdatePriceDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.entity.Product;
import com.fos.reporting.repository.InventoryRepository;
import com.fos.reporting.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService,
                               InventoryRepository inventoryRepository,
                               ProductRepository productRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/inventory")
    public ResponseEntity<String> addEntry(@RequestBody @Valid InventoryDto inventoryDto) {
        try {
            // Validate that currentLevel doesn't exceed tankCapacity
            if (inventoryDto.getCurrentLevel() > inventoryDto.getTankCapacity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Current level cannot exceed tank capacity");
            }

            if (inventoryService.addToInventory(inventoryDto)) {
                return ResponseEntity.ok("Added to inventory. Booking limit calculated as: " +
                        (inventoryDto.getTankCapacity() - inventoryDto.getCurrentLevel()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add inventory");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/inventory/price")
    public ResponseEntity<Float> getPriceFromInventory(@RequestParam String productName) {
        return productRepository.findByNameIgnoreCase(productName)
                .map(product -> inventoryRepository.findTopByProductOrderByLastUpdatedDesc(product)
                        .map(inventory -> ResponseEntity.ok(inventory.getPrice()))
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inventory/latest")
    public ResponseEntity<List<Inventory>> getLatestInventoryPerProduct() {
        List<Product> distinctProducts = inventoryRepository.findDistinctProducts();
        List<Inventory> latest = new ArrayList<>();
        for (Product product : distinctProducts) {
            inventoryRepository.findTopByProductOrderByLastUpdatedDesc(product).ifPresent(latest::add);
        }
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/inventory/booking-limit")
    public ResponseEntity<Float> getBookingLimit(@RequestParam String productName) {
        return productRepository.findByNameIgnoreCase(productName)
                .map(product -> inventoryRepository.findTopByProductOrderByLastUpdatedDesc(product)
                        .map(inventory -> ResponseEntity.ok(inventory.getBookingLimit()))
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<String> updateInventory(@PathVariable Long id, @RequestBody @Valid InventoryDto inventoryDto) {
        try {
            // Validate that currentLevel doesn't exceed tankCapacity
            if (inventoryDto.getCurrentLevel() > inventoryDto.getTankCapacity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Current level cannot exceed tank capacity");
            }

            boolean updated = inventoryService.updateInventory(id, inventoryDto);
            if (updated) {
                return ResponseEntity.ok("Inventory updated. New booking limit: " +
                        (inventoryDto.getTankCapacity() - inventoryDto.getCurrentLevel()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/inventory/update-price")
    public ResponseEntity<String> updateProductPrice(@RequestBody @Valid UpdatePriceDto dto) {
        boolean updated = inventoryService.updatePrice(dto.getProductName(), dto.getNewPrice());
        if (updated) {
            return ResponseEntity.ok("Price updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in inventory");
        }
    }
}
