package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collections {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dateTime;
    private Long employeeId;
    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private float shortCollections;
    private double expectedTotal;
    private double receivedTotal;
    private double difference;
    private String entryId;
}
