package com.fos.reporting.domain;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerDto {
    private String customerName;
    private String customerVehicle;
    private Long employeeId;
    private Double amountBorrowed;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;
    private String notes;
    private String address;
    private String phone;
    private String email;
}