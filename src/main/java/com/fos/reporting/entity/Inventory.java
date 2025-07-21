package com.fos.reporting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryID;
    @NotNull
    private float quantity;
    @NotNull
    private int tankCapacity;
    @NotNull
    private float currentLevel;
    @NotNull
    private float bookingLimit; // This will be calculated automatically
    @NotNull
    private LocalDateTime lastUpdated;
    @NotNull
    @Column(name = "employee_id")
    private Long employeeId;
    @NotNull
    private float price;
    private LocalDateTime lastPriceUpdated;
    @Column(nullable = false)
    private String metric = "liters";
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Automatically calculate booking limit before persisting
    @PrePersist
    @PreUpdate
    private void calculateBookingLimit() {
        this.bookingLimit = this.tankCapacity - this.currentLevel;
        if (this.lastUpdated == null) {
            this.lastUpdated = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        }
    }

    // Validation method
    public boolean isValidLevel() {
        return currentLevel >= 0 && currentLevel <= tankCapacity;
    }
}
