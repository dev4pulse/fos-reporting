package com.fos.reporting.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InventoryDto {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "Quantity cannot be null.") // This allows positive and negative numbers, but not zero.
    private Double quantity;

    private Long employeeId;

    @NotBlank(message = "Metric (unit of measurement) cannot be blank.")
    private String metric;
}