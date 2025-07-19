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
    private String name;
    private Double amount;
    private LocalDateTime borrowedAt;
    private Integer borrowerId;
    private LocalDate borrowedDate;
    private Double amountBorrowed;
    private String customerId;
    private Long employeeId;
    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collections collection;
}
