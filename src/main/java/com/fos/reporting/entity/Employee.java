package com.fos.reporting.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long employeeId;

    @NotNull
    private String employeeFirstName;
    @NotNull
    private String employeeLastName;
    @Email
    private String employeeEmail;
    private Long employeePhoneNumber;
    @NotNull
    private String employeeRole;
    @NotNull
    private Integer employeeSalary;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private boolean isActive;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
}