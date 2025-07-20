package com.fos.reporting.domain;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
@Data
public class EmployeeDto {
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    @Email
    private String employeeEmail;
    private Long employeePhoneNumber;
    private String employeeRole;
    private Integer employeeSalary;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private String username;
    private String password;
}