package com.fos.reporting.service;

import com.fos.reporting.domain.DocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DocumentStorageService {
    DocumentDto uploadDocument(MultipartFile file, String documentType, LocalDate expiryDate,
                               String issuingAuthority, LocalDate issueDate, Integer renewalPeriodDays,
                               String responsibleParty, String notes);

    List<DocumentDto> listAllDocuments();

    String generateDownloadUrl(String fileName);
}