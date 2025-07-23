package com.fos.reporting.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowerDto {
    private Long id;

    @NotBlank(message = "Customer name cannot be blank")
    @Size(max = 255)
    private String customerName;

    @Size(max = 50)
    private String customerVehicle;

    @Size(max = 50)
    private String employeeId;

    @NotNull(message = "Amount borrowed cannot be null")
    private Double amountBorrowed;

    private LocalDateTime borrowDate;
    private LocalDate dueDate;

    @Size(max = 50)
    private String status;

    @Size(max = 1000)
    private String notes;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String email;
}