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
        String productName = dto.getProductName().trim().toLowerCase();
        float incomingQty = dto.getQuantity();

        Optional<Inventory> latestOpt = inventoryRepository
                .findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(productName);

        Inventory newInventory = new Inventory();
        newInventory.setProductName(productName);
        newInventory.setProductID(dto.getProductID());
        newInventory.setQuantity(incomingQty);
        newInventory.setTankCapacity(dto.getTankCapacity());
        newInventory.setCurrentLevel(dto.getCurrentLevel());
        newInventory.setEmployeeId(dto.getEmployeeId());
        newInventory.setLastUpdated(LocalDateTime.now());
        newInventory.setPrice(dto.getPrice() > 0 ? dto.getPrice() : getDefaultPrice(productName));
        newInventory.setMetric(dto.getMetric() != null ? dto.getMetric() : "liters");

        float newBookingLimit = dto.getBookingLimit();
        if (latestOpt.isPresent()) {
            Inventory latest = latestOpt.get();
            float prevQty = latest.getQuantity();
            float diff = prevQty - incomingQty;
            if (diff > 0.01) {
                newBookingLimit = (float) (latest.getBookingLimit() * 1.1);
            } else {
                newBookingLimit = Math.max(latest.getBookingLimit(), dto.getBookingLimit());
            }

        }


        newInventory.setBookingLimit(newBookingLimit);

        inventoryRepository.save(newInventory);
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