package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_log")
@Data
@NoArgsConstructor
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double currentLevel;

    @Column(nullable = false)
    private String metric;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    private Long employeeId;
}