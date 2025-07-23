package com.fos.reporting.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryRecordDto {
    private Long inventoryId;
    private Long productId;
    private String productName;
    private Double quantity;
    private Double currentLevel;
    private String metric;
    private LocalDateTime transactionDate;
    private Long employeeId;
}