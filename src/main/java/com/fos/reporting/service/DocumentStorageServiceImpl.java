package com.fos.reporting.service;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.entity.Document;
import com.fos.reporting.exception.DocumentNotFoundException;
import com.fos.reporting.exception.FileUploadException;
import com.fos.reporting.repository.DocumentRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private final Storage storage;
    private final DocumentRepository documentRepository;
    private final String bucketName;

    public DocumentStorageServiceImpl(Storage storage,
                                      DocumentRepository documentRepository,
                                      @Value("${gcs.bucket.name}") String bucketName) {
        this.storage = storage;
        this.documentRepository = documentRepository;
        this.bucketName = bucketName;
    }

    @Override
    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, String documentType, LocalDate expiryDate,
                                      String issuingAuthority, LocalDate issueDate, Integer renewalPeriodDays,
                                      String responsibleParty, String notes) {

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new FileUploadException("Invalid filename. Contains relative path sequence: " + originalFilename);
        }

        String blobName = UUID.randomUUID().toString() + "-" + originalFilename;
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {
            storage.create(blobInfo, file.getInputStream());

            Document document = new Document();
            document.setOriginalFilename(originalFilename);
            document.setBlobName(blobName); // Store the unique GCS name
            document.setDocumentType(documentType);
            document.setUploadTimestamp(LocalDateTime.now());
            document.setIssuingAuthority(issuingAuthority);
            document.setIssueDate(issueDate);
            document.setExpiryDate(expiryDate);
            document.setRenewalPeriodDays(renewalPeriodDays);
            document.setResponsibleParty(responsibleParty);
            document.setNotes(notes);
            document.setFileSize(file.getSize()); // Store file size
            document.setContentType(file.getContentType()); // Store content type

            Document savedDocument = documentRepository.save(document);
            return convertToDto(savedDocument);

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file '" + file.getOriginalFilename() + "' to Google Cloud Storage.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> listAllDocuments() {
        // Use the sorted query for consistency
        return documentRepository.findAllByOrderByUploadTimestampDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadDocumentAsResource(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        Blob blob = storage.get(BlobId.of(bucketName, document.getBlobName()));
        if (blob == null || !blob.exists()) {
            throw new DocumentNotFoundException("File not found in storage for document id: " + documentId);
        }

        // Return the file's content as a resource
        return new ByteArrayResource(blob.getContent()) {
            // Override getFilename() to return the original, user-friendly name
            @Override
            public String getFilename() {
                return document.getOriginalFilename();
            }
        };
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Cannot delete. Document not found with id: " + documentId));

        BlobId blobId = BlobId.of(bucketName, document.getBlobName());
        boolean deleted = storage.delete(blobId);

        if (!deleted) {
            // This can happen if the file was already deleted from GCS but not the DB.
            // Depending on requirements, you might log this as a warning or ignore it.
            System.err.println("Warning: File not found in GCS for blobName: " + document.getBlobName() + ", but deleting DB record anyway.");
        }

        documentRepository.delete(document);
    }

    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setOriginalFilename(document.getOriginalFilename()); // Changed from fileName
        dto.setDocumentType(document.getDocumentType());
        dto.setUploadTimestamp(document.getUploadTimestamp());
        dto.setIssuingAuthority(document.getIssuingAuthority());
        dto.setIssueDate(document.getIssueDate());
        dto.setExpiryDate(document.getExpiryDate());
        dto.setRenewalPeriodDays(document.getRenewalPeriodDays());
        dto.setResponsibleParty(document.getResponsibleParty());
        dto.setNotes(document.getNotes());
        dto.setFileSize(document.getFileSize());
        dto.setContentType(document.getContentType());
        // We no longer return a static fileUrl
        return dto;
    }
}