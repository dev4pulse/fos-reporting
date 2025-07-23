package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrower {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    private String customerVehicle;
    private String employeeId;
    private Double amountBorrowed;
    private LocalDateTime borrowDate;
    private LocalDate dueDate;
    private String status;
    private String notes;
    private String address;
    private String phone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collections collection;
}