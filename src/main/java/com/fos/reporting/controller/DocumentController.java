package com.fos.reporting.controller;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.service.DocumentStorageService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping
    public ResponseEntity<List<DocumentDto>> listAllDocuments() {
        List<DocumentDto> documents = documentStorageService.listAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Resource resource = documentStorageService.loadDocumentAsResource(documentId);

        String filename = resource.getFilename();
        if (filename == null) {
            filename = "downloaded-file";
        }

        // Dynamically determine content type from the stored metadata
        DocumentDto docInfo = documentStorageService.listAllDocuments().stream()
                .filter(d -> d.getId().equals(documentId)).findFirst().orElse(null);
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        if (docInfo != null && docInfo.getContentType() != null) {
            contentType = MediaType.parseMediaType(docInfo.getContentType());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentType(contentType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * Deletes a document from the database and the storage provider.
     *
     * @param documentId The ID of the document to delete.
     * @return A response entity with a 204 No Content status.
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentStorageService.deleteDocument(documentId);
        return ResponseEntity.noContent().build(); // Standard practice for successful DELETE
    }
}