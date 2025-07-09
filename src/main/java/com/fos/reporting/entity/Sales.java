package com.fos.reporting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
public class Sales {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private String productName;
    @Column(name = "gun")
    private String gun;
    @NotNull
    private int employeeId;
    private float openingStock;
    private float closingStock;
    private float testingTotal;
    @Column(name = "sales_in_liters")
    private BigDecimal salesInLiters;
    private float price;
    @Column(name = "sales_in_rupees")
    private float salesInRupees;
}