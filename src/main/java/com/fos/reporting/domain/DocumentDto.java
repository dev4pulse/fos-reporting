package com.fos.reporting.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DocumentDto {
    private Long id;
    private String fileName;
    private String documentType;
    private String fileUrl;
    private LocalDateTime uploadTimestamp;
    private LocalDate expiryDate;
    private String issuingAuthority;
    private LocalDate issueDate;
    private Integer renewalPeriodDays;
    private String responsibleParty;
    private String notes;
}