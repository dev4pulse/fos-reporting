package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private int productID;
    private String productName;
    private float quantity;
    private int tankCapacity;
    private float currentLevel;
    private float bookingLimit;
    private Long employeeId;
    private float price;
}
