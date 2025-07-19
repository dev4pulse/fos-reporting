package com.fos.reporting.domain;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerDto {
    private String name;
    private Double amount;
    private String customerId;
    private Long employeeId;
    private LocalDate borrowedDate;
    private Double amountBorrowed;
    private Integer borrowerId;
}