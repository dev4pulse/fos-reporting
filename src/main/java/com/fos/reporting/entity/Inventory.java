package com.fos.reporting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Inventory")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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
    private LocalDateTime lastUpdated; // Changed from Date to LocalDateTime for better precision
    @NotNull
    private Long employeeId;
}
