package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String documentType; // e.g., "Trade License", "Pollution Certificate"

    @Column(nullable = false, length = 1024)
    private String fileUrl; // The URL to the file in S3

    private LocalDate expiryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadTimestamp;
}