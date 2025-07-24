package com.fos.reporting.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing the current inventory status of a single product.
 * Used for dashboard or overview screens.
 */
@Data
@NoArgsConstructor
public class ProductInventoryStatusDto {

    // Product Information
    private Long productId;
    private String productName;
    private ProductStatus status;
    private Integer tankCapacity;
    private BigDecimal currentPrice;

    // Latest Inventory Information
    private Double currentLevel;
    private String metric;
    private LocalDateTime lastUpdated;

    /**
     * Constructor to easily create this DTO from a Product entity.
     * Initializes inventory to a default "zero" state.
     */
    public ProductInventoryStatusDto(com.fos.reporting.entity.Product product) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.status = product.getStatus();
        this.tankCapacity = product.getTankCapacity();
        this.currentPrice = product.getPrice();
        this.currentLevel = 0.0; // Default to 0 if no inventory logs exist
        this.metric = "N/A";
        this.lastUpdated = null;
    }
}