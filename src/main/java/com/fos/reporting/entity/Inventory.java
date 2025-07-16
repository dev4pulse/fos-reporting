package com.fos.reporting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long inventoryID;
    @NotNull
    private int productID;
    @NotNull
    private String productName;
    @NotNull
    private float quantity;
    @NotNull
    private int tankCapacity;
    @NotNull
    private float currentLevel;
    @NotNull
    private float bookingLimit;
    @NotNull
    private LocalDateTime lastUpdated;
    @NotNull
    private Long employeeId;
    @NotNull
    private float price;
    private LocalDateTime lastPriceUpdated;
    @Column(nullable = false)
    private String metric = "liters"; // default at the entity level (optional)
}