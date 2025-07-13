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

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public boolean addToInventory(InventoryDto dto) {
        Inventory inventory = new Inventory();
        inventory.setProductName(dto.getProductName());
        inventory.setProductID(dto.getProductID());
        inventory.setQuantity(dto.getQuantity());
        inventory.setTankCapacity(dto.getTankCapacity());
        inventory.setCurrentLevel(dto.getCurrentLevel());
        inventory.setBookingLimit(dto.getBookingLimit());
        inventory.setEmployeeId(dto.getEmployeeId());
        inventory.setLastUpdated(LocalDateTime.now());

        // Set price explicitly from DTO if provided
        if (dto.getPrice() > 0) {
            inventory.setPrice(dto.getPrice());
        } else {
            // Otherwise set default price based on product name
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

}