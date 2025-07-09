package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class InventoryDto {
    @NotNull
    private String productName;
    @NotNull
    private float quantity;
    @NotNull
    private LocalDateTime lastUpdated;
    @NotNull
    private int employeeId;
}