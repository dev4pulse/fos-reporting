package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    @NotNull
    private String productName;
    @NotNull
    private float quantity;
    @NotNull
    private LocalDateTime lastUpdated; // Changed from Date to LocalDateTime for better precision
    @NotNull
    private int employeeId;
}
