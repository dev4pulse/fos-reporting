package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profit_loss")
public class ProfitLoss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cashReceived;      // Latest total cash received
    private double inventoryValue;    // Latest inventory value (currentLevel * price)
    private double totalExpenses;     // Latest total expenses
    private double profitLoss;        // Calculated P/L
    private LocalDateTime calculatedAt; // Timestamp of calculation


}
