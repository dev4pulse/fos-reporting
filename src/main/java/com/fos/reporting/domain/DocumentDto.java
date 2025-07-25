package com.fos.reporting.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Document metadata.
 * Used for API responses to avoid exposing the internal entity structure.
 */
@Data
public class DocumentDto {

    private Long id;
    private String originalFilename;
    private String documentType;
    private String issuingAuthority;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Integer renewalPeriodDays;
    private String responsibleParty;
    private String notes;

    private Long fileSize;
    private String contentType;

    private LocalDateTime uploadTimestamp;
}