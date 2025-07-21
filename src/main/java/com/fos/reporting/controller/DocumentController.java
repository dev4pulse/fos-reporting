package com.fos.reporting.controller;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.service.DocumentStorageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentStorageService documentStorageService;

    public DocumentController(DocumentStorageService documentStorageService) {
        this.documentStorageService = documentStorageService;
    }

    /**
     * POST /api/documents : Uploads a new document with its metadata.
     * This is a multipart request.
     */
    @PostMapping
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "expiryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {

        DocumentDto uploadedDocument = documentStorageService.uploadDocument(file, documentType, expiryDate);
        return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
    }

    /**
     * GET /api/documents : Retrieves a list of all uploaded documents and their metadata.
     */
    @GetMapping
    public ResponseEntity<List<DocumentDto>> listAllDocuments() {
        List<DocumentDto> documents = documentStorageService.listAllDocuments();
        return ResponseEntity.ok(documents);
    }
}