package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user-facing filename
    private String originalFilename;

    // The unique name of the file in the GCS bucket (e.g., UUID-based)
    @Column(nullable = false, unique = true)
    private String blobName;

    private String documentType;
    private String issuingAuthority;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Integer renewalPeriodDays;
    private String responsibleParty;

    @Lob // For potentially long notes
    private String notes;

    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadTimestamp;

}