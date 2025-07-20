package com.fos.reporting.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseDto {

    private Long id;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotNull(message = "Expense date cannot be null")
    private LocalDate expenseDate;

    private Long employeeId;
}