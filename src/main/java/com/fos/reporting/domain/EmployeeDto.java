package com.fos.reporting.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    @NotNull
    private Long employeeId;
    @NotNull
    private String employeeFirstName;
    @NotNull
    private String employeeLastName;
    @Email
    private String employeeEmail;
    private int employeePhoneNumber;
    @NotNull
    private String employeeRole;
    @NotNull
    private int employeeSalary;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private boolean isActive;
    @NotNull
    private String username;
    @NotNull
    private String password;
}
