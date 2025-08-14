package com.fos.reporting.service;

import com.fos.reporting.domain.InventoryDto;
import com.fos.reporting.domain.InventoryRecordDto;
import com.fos.reporting.domain.ProductInventoryStatusDto;
import com.fos.reporting.domain.ProductStatus;
import com.fos.reporting.entity.InventoryLog;
import com.fos.reporting.entity.Product;
import com.fos.reporting.repository.InventoryLogRepository;
import com.fos.reporting.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryLogRepository inventoryLogRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryLogRepository inventoryLogRepository, ProductRepository productRepository) {
        this.inventoryLogRepository = inventoryLogRepository;
        this.productRepository = productRepository;
    }
    @Transactional(readOnly = true)
    public List<ProductInventoryStatusDto> getLatestInventoryForAllProducts() {
        // 1. Fetch all products. This will be the base of our response.
        List<Product> allProducts = productRepository.findAll();

        // 2. Fetch all the latest inventory logs in a single, efficient query.
        List<InventoryLog> latestLogs = inventoryLogRepository.findLatestLogForEachProduct();

        // 3. Convert the list of logs into a Map for quick lookups (O(1) access).
        //    The key is the product ID.
        Map<Long, InventoryLog> latestLogsMap = latestLogs.stream()
                .collect(Collectors.toMap(log -> log.getProduct().getId(), log -> log));

        // 4. Map the products to our DTO, enriching them with inventory data.
        return allProducts.stream().map(product -> {
            ProductInventoryStatusDto dto = new ProductInventoryStatusDto(product);
            InventoryLog latestLog = latestLogsMap.get(product.getId());

            if (latestLog != null) {
                // If an inventory log exists for this product, update the DTO
                dto.setCurrentLevel(latestLog.getCurrentLevel());
                dto.setMetric(latestLog.getMetric());
                dto.setLastUpdated(latestLog.getTransactionDate());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // src/main/java/com/fos/reporting/service/InventoryService.java
    @Transactional
    public InventoryRecordDto recordInventoryTransaction(InventoryDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Cannot record inventory for an INACTIVE product: " + product.getName());
        }

        Optional<InventoryLog> latestLogOptional = inventoryLogRepository.findTopByProductIdOrderByTransactionDateDesc(product.getId());
        double previousLevel = latestLogOptional.map(InventoryLog::getCurrentLevel).orElse(0.0);
        double newCurrentLevel = previousLevel + dto.getQuantity();

        if (newCurrentLevel > product.getTankCapacity()) {
            throw new IllegalStateException(
                    String.format("Adding quantity %.2f would exceed tank capacity of %d for product '%s'. Current level is %.2f.",
                            dto.getQuantity(),
                            product.getTankCapacity(),
                            product.getName(),
                            previousLevel
                    )
            );
        }

        InventoryLog log = new InventoryLog();
        log.setProduct(product);
        log.setQuantity(dto.getQuantity());
        log.setCurrentLevel(newCurrentLevel);
        log.setMetric(dto.getMetric());
        log.setEmployeeId(dto.getEmployeeId());
        log.setTransactionDate(LocalDateTime.now());
        log.setEntryId(dto.getEntryId()); // <-- Add this line

        InventoryLog savedLog = inventoryLogRepository.save(log);
        return toRecordDto(savedLog);
    }
    @Transactional(readOnly = true)
    public List<InventoryRecordDto> getHistoryForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        return inventoryLogRepository.findByProductIdOrderByTransactionDateDesc(productId)
                .stream()
                .map(this::toRecordDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<InventoryRecordDto> getAllInventoryHistory() {
        // This is more consistent and safer than returning the raw entity list.
        // It assumes InventoryLogRepository has a method `findAllByOrderByTransactionDateDesc()`
        return inventoryLogRepository.findAllByOrderByTransactionDateDesc()
                .stream()
                .map(this::toRecordDto)
                .collect(Collectors.toList());
    }

    private InventoryRecordDto toRecordDto(InventoryLog log) {
        InventoryRecordDto dto = new InventoryRecordDto();
        dto.setInventoryId(log.getId());
        dto.setQuantity(log.getQuantity());
        dto.setCurrentLevel(log.getCurrentLevel());
        dto.setMetric(log.getMetric());
        dto.setTransactionDate(log.getTransactionDate());
        dto.setEmployeeId(log.getEmployeeId());

        if (log.getProduct() != null) {
            dto.setProductId(log.getProduct().getId());
            dto.setProductName(log.getProduct().getName());
        }
        return dto;
    }

}