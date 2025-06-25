package com.fos.reporting.service;

import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.repository.InventoryRepository;
import jakarta.persistence.Access;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    @Autowired private InventoryRepository inventoryRepository;

    public boolean addToInventory(InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDto, inventory);
        int tankCapacity = 25000;
        inventory.setBookingLimit(tankCapacity-inventoryDto.getQuantity());
        inventoryRepository.save(inventory);
        return true;
    }
}
