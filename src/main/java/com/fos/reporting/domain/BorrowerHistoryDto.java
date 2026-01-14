package com.fos.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerHistoryDto {
    private Long id;
    private String employeeId;
    private Long borrowerId;
    private String customerName;
    private String customerVehicle;
    private String phone;
    private String status;
    private LocalDateTime borrowDate;
    private LocalDate dueDate;
    private Double amountBorrowed;
    private Double duePaid;
    private Double extraBorrowed;
    private Double remainingAmount;
    private String transactionType;
    private LocalDateTime updatedAt;


}
