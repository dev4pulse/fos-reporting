package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collections")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collections {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dateTime;

    private int employeeId;
    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private float borrowedAmount;
    private float debtRecovered;
    private String borrower;

    // ✅ Updated field mapping for renamed DB column
    @Column(name = "short_collections")
    private float shortCollections;

    private double expectedTotal;
    private double receivedTotal;
    private double difference;
}
