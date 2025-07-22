package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class InventoryDto {
    @NotNull
    private Long productId;
    private float quantity;
    @NotNull
    private int tankCapacity;
    @NotNull
    private float currentLevel;
    private Long employeeId;
    private float price;
    private String metric;
}