package com.fos.reporting.service;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.entity.Product;
import com.fos.reporting.repository.InventoryRepository;
import com.fos.reporting.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class InventoryService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean addToInventory(InventoryDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + dto.getProductId()));

        float incomingQty = dto.getQuantity();
        int tankCapacity = dto.getTankCapacity();
        float currentLevel = dto.getCurrentLevel();

        Inventory newInventory = new Inventory();
        newInventory.setProduct(product);
        newInventory.setQuantity(incomingQty);
        newInventory.setTankCapacity(tankCapacity);
        newInventory.setCurrentLevel(currentLevel);

        // Calculate booking limit as tank capacity - current level
        float bookingLimit = tankCapacity - currentLevel;
        newInventory.setBookingLimit(bookingLimit);

        newInventory.setEmployeeId(dto.getEmployeeId());
        newInventory.setLastUpdated(LocalDateTime.now(IST_ZONE));
        newInventory.setPrice(dto.getPrice() > 0 ? dto.getPrice() : getDefaultPrice(product.getName()));
        newInventory.setMetric(dto.getMetric() != null ? dto.getMetric() : "liters");

        inventoryRepository.save(newInventory);
        return true;
    }

    public boolean updatePrice(String productName, float newPrice) {
        Product product = productRepository.findByNameIgnoreCase(productName)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productName));

        Optional<Inventory> latest = inventoryRepository.findTopByProductOrderByLastUpdatedDesc(product);
        if (latest.isPresent()) {
            Inventory previous = latest.get();
            Inventory updatedEntry = new Inventory();
            BeanUtils.copyProperties(previous, updatedEntry);
            updatedEntry.setPrice(newPrice);
            updatedEntry.setLastPriceUpdated(LocalDateTime.now(IST_ZONE));

            // Recalculate booking limit
            float bookingLimit = updatedEntry.getTankCapacity() - updatedEntry.getCurrentLevel();
            updatedEntry.setBookingLimit(bookingLimit);

            updatedEntry.setInventoryID(null);
            inventoryRepository.save(updatedEntry);
            return true;
        }
        return false;
    }

    public boolean updateInventory(Long id, InventoryDto dto) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(id);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();

            if (dto.getProductId() != null) {
                Product product = productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                inventory.setProduct(product);
            }

            // Update basic properties
            if (dto.getQuantity() > 0) inventory.setQuantity(dto.getQuantity());
            if (dto.getTankCapacity() > 0) inventory.setTankCapacity(dto.getTankCapacity());
            if (dto.getCurrentLevel() >= 0) inventory.setCurrentLevel(dto.getCurrentLevel());
            if (dto.getEmployeeId() != null) inventory.setEmployeeId(dto.getEmployeeId());
            if (dto.getPrice() > 0) inventory.setPrice(dto.getPrice());
            if (dto.getMetric() != null) inventory.setMetric(dto.getMetric());

            // Recalculate booking limit based on updated values
            float bookingLimit = inventory.getTankCapacity() - inventory.getCurrentLevel();
            inventory.setBookingLimit(bookingLimit);

            inventory.setLastUpdated(LocalDateTime.now(IST_ZONE));
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    private float getDefaultPrice(String productName) {
        return switch (productName.toLowerCase()) {
            case "petrol" -> 104.5f;
            case "diesel" -> 92.0f;
            default -> 100.0f;
        };
    }
}
