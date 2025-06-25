package com.fos.reporting.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Employee")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
