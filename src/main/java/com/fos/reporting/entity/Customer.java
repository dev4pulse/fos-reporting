package com.fos.reporting.entity;

import java.time.LocalDate;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;    // ← single combined column

    @Email
    private String customerEmail;

    
    private int customerPhoneNumber;

    private String vehicleNumber;

    @Column(name = "registered_date")
    private LocalDate registeredDate;
}
