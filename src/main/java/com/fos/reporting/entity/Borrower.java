package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrowers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowerId;

    @ManyToOne(optional = true)                   // allow nulls
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @ManyToOne(optional = true)                   // allow nulls
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    @Column(name = "amount_borrowed", nullable = true)  // allow nulls
    private Float amountBorrowed;                        // use object type so you can have null

    @Column(name = "borrowed_date", nullable = true)    // allow nulls
    private LocalDateTime borrowedDate;
}
