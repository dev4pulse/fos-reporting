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
        if (dto.getPrice() > 0) {
            inventory.setPrice(dto.getPrice());
        } else {
            inventory.setPrice(getDefaultPrice(dto.getProductName()));
        }
        inventoryRepository.save(inventory);
        return true;
    }

    private float getDefaultPrice(String productName) {
        switch (productName.toLowerCase()) {
            case "petrol":
                return 104.5f;
            case "diesel":
                return 92.0f;
            default:
                return 100.0f;
        }
    }

    public boolean updatePrice(String productName, float newPrice) {
        Optional<Inventory> latest = inventoryRepository.findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(productName);
        if (latest.isPresent()) {
            Inventory updatedEntry = new Inventory();
            Inventory previous = latest.get();
            updatedEntry.setProductName(previous.getProductName());
            updatedEntry.setProductID(previous.getProductID());
            updatedEntry.setQuantity(previous.getQuantity());
            updatedEntry.setTankCapacity(previous.getTankCapacity());
            updatedEntry.setCurrentLevel(previous.getCurrentLevel());
            updatedEntry.setBookingLimit(previous.getBookingLimit());
            updatedEntry.setEmployeeId(previous.getEmployeeId());
            updatedEntry.setLastUpdated(LocalDateTime.now());
            updatedEntry.setPrice(newPrice);
            inventoryRepository.save(updatedEntry);
            return true;
        }
        return false;
    }
}