package com.fos.reporting.service;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.entity.Inventory;
import com.fos.reporting.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;                     // ← for List
import java.util.stream.Collectors;       // ← for Collectors

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional
    public boolean addToInventory(InventoryDto dto) {
        Inventory inv = new Inventory();
        // copy fields from DTO into your entity
        inv.setProductName(dto.getProductName());
        inv.setQuantity(dto.getQuantity());
        //converting float into int
        inv.setTankCapacity(dto.getTotalCapacity().intValue());
        inv.setLastUpdated(dto.getLastUpdated());
        // if you still have an enterPrice on your entity, set it here:
        // inv.setEnterPrice(dto.getEnterPrice());

        inventoryRepository.save(inv);
        return true;
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> findAll() {
        return inventoryRepository.findAll().stream()
            .map(entity -> {
                InventoryDto dto = new InventoryDto();
                dto.setProductName(entity.getProductName());
                dto.setQuantity(entity.getQuantity());
                dto.setTotalCapacity((float)entity.getTankCapacity());
                dto.setLastUpdated(entity.getLastUpdated());
                // dto.setEnterPrice(entity.getEnterPrice());
                return dto;
            })
            .collect(Collectors.toList());
    }
}
