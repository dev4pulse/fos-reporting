package com.fos.reporting.controller;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.service.DocumentStorageService;
import jakarta.validation.constraints.NotBlank;
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
@Validated // Enables validation for method parameters
public class DocumentController {

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

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DocumentDto uploadedDocument = documentStorageService.uploadDocument(
                file, documentType, expiryDate,
                issuingAuthority, issueDate, renewalPeriodDays,
                responsibleParty, notes
        );
        return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of all uploaded documents and their metadata,
     * sorted by upload timestamp in descending order.
     *
     * @return A response entity containing the list of all documents.
     */
    @GetMapping
    public ResponseEntity<List<DocumentDto>> listAllDocuments() {
        List<DocumentDto> documents = documentStorageService.listAllDocuments();
        return ResponseEntity.ok(documents);
    }
}