package com.fos.reporting.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "employee")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeId;
    @NotNull
    private String employeeFirstName;
    @NotNull
    private String employeeLastName;
    @Email
    private String employeeEmail;
    private long employeePhoneNumber;
    @NotNull
    private String employeeRole;
    @NotNull
    private int employeeSalary;
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