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

    // This is "Document Name/Type"
    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false, length = 1024)
    private String fileUrl;

    // --- New Fields ---
    private String issuingAuthority;

    private LocalDate issueDate;

    // This is "Expiration Date" (already exists)
    private LocalDate expiryDate;

    // e.g., 30, 60, 90 days before expiry
    private Integer renewalPeriodDays;

    private String responsibleParty;

    @Column(length = 2048) // Increased length for longer notes
    private String notes;
    // --- End of New Fields ---

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadTimestamp;
}