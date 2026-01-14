package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_history")
public class BorrowerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id")
    private Borrower borrower;
    private String customerName;
    private String customerVehicle;
    private String employeeId;
    private Double amountBorrowed;
    private LocalDateTime borrowDate;
    private LocalDate dueDate;
    private String status;
    private String phone;
    private Double duePaid;
    private Double extraBorrowed;
    private Double remainingAmount; // NEW
    private LocalDateTime updatedAt;
}
