package com.fos.reporting.service;

import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.repository.InventoryRepository;
import jakarta.persistence.Access;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    private float getDefaultPrice(String productName) {
        return switch (productName.toLowerCase()) {
            case "petrol" -> 104.5f;
            case "diesel" -> 92.0f;
            default -> 100.0f;
        };
    }

    public boolean addToInventory(InventoryDto dto) {
        Inventory inventory = new Inventory();
        inventory.setProductName(dto.getProductName().trim().toLowerCase());
        inventory.setProductID(dto.getProductID());
        inventory.setQuantity(dto.getQuantity());
        inventory.setTankCapacity(dto.getTankCapacity());
        inventory.setCurrentLevel(dto.getCurrentLevel());
        inventory.setBookingLimit(dto.getBookingLimit());
        inventory.setEmployeeId(dto.getEmployeeId());
        inventory.setLastUpdated(LocalDateTime.now());
        if (dto.getMetric() != null && !dto.getMetric().isBlank()) {
            inventory.setMetric(dto.getMetric().toLowerCase());
        } else {
            inventory.setMetric("liters"); // default value
        }

        if (dto.getPrice() > 0) {
            inventory.setPrice(dto.getPrice());
            inventory.setLastPriceUpdated(LocalDateTime.now()); // track last price update
        } else {
            float defaultPrice = getDefaultPrice(dto.getProductName());
            inventory.setPrice(defaultPrice);
            inventory.setLastPriceUpdated(LocalDateTime.now());
        }

        inventoryRepository.save(inventory);
        return true;
    }

    public boolean updatePrice(String productName, float newPrice) {
        Optional<Inventory> latest = inventoryRepository.findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(productName);
        if (latest.isPresent()) {
            Inventory previous = latest.get();
            Inventory updatedEntry = new Inventory();
            BeanUtils.copyProperties(previous, updatedEntry);
            updatedEntry.setPrice(newPrice);
            updatedEntry.setLastUpdated(LocalDateTime.now());
            updatedEntry.setLastPriceUpdated(LocalDateTime.now()); // also update this

            inventoryRepository.save(updatedEntry);
            return true;
        }
        return false;
    }
}