package com.fos.reporting.entity;

import com.fos.reporting.domain.ProductStatus; // Import the new enum
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer tankCapacity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // --- NEW FIELD ---
    /**
     * The current status of the product (e.g., ACTIVE, INACTIVE).
     * Stored as a string in the database for readability.
     */
    @Enumerated(EnumType.STRING) // Best practice: stores "ACTIVE" or "INACTIVE" in the DB
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE; // Default new products to ACTIVE
}