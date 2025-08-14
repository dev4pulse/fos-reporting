package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private String employeeName;
    private String role;
    private String status;

    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String timeAtWork;

    private LocalDate joinedDate;
    private LocalDate date;
}