package com.fos.reporting.controller;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.service.DocumentStorageService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Validated
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentStorageService documentStorageService;

    public DocumentController(DocumentStorageService documentStorageService) {
        this.documentStorageService = documentStorageService;
    }

    @PostMapping
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") @NotBlank(message = "Document type cannot be blank") String documentType,
            @RequestParam(value = "issuingAuthority", required = false) String issuingAuthority,
            @RequestParam(value = "issueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(value = "expiryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam(value = "renewalPeriodDays", required = false) Integer renewalPeriodDays,
            @RequestParam(value = "responsibleParty", required = false) String responsibleParty,
            @RequestParam(value = "notes", required = false) String notes) {

        logger.info("Received document upload request: documentType={}, issuingAuthority={}, issueDate={}, expiryDate={}, renewalPeriodDays={}, responsibleParty={}, notes={}",
                documentType, issuingAuthority, issueDate, expiryDate, renewalPeriodDays, responsibleParty, notes);

        if (file.isEmpty()) {
            logger.warn("Upload failed: file is empty");
            return ResponseEntity.badRequest().build();
        }

        try {
            DocumentDto uploadedDocument = documentStorageService.uploadDocument(
                    file, documentType, expiryDate,
                    issuingAuthority, issueDate, renewalPeriodDays,
                    responsibleParty, notes
            );
            logger.info("Document uploaded successfully: id={}", uploadedDocument.getId());
            return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> listAllDocuments() {
        logger.info("Received request to list all documents");
        try {
            List<DocumentDto> documents = documentStorageService.listAllDocuments();
            logger.info("Fetched {} documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("Error fetching documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<String> generateDownloadUrl(@PathVariable String fileName) {
        logger.info("Received request to generate download URL for file: {}", fileName);
        try {
            String url = documentStorageService.generateDownloadUrl(fileName);
            logger.info("Generated download URL for file: {}", fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            logger.error("Error generating download URL for file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}