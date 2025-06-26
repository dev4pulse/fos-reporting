package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastUpdated; // Changed from Date to LocalDateTime for better precision
    @NotNull
    private int employeeId;
}
