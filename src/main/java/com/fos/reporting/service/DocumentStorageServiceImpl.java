package com.fos.reporting.service;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.entity.Document;
import com.fos.reporting.exception.FileUploadException;
import com.fos.reporting.repository.DocumentRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private final Storage storage;
    private final DocumentRepository documentRepository;
    private final String bucketName;

    public DocumentStorageServiceImpl(@Qualifier("serviceAccountStorage") Storage storage, // Injected by Spring Cloud GCP
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
        // --- Improvement 1: Sanitize the filename for security ---
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new FileUploadException("Invalid filename. Contains relative path sequence: " + originalFilename);
        }

        // 1. Generate a unique blob name for the file in GCS
        String blobName = UUID.randomUUID().toString() + "-" + originalFilename;

        // 2. Configure the blob for upload
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {
            // --- Improvement 2: Use InputStream for memory efficiency ---
            // This streams the file directly, avoiding loading it all into memory.
            storage.create(blobInfo, file.getInputStream());

            // 4. Construct the public URL of the uploaded file
            String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, blobName);

            // 5. Create and save the document metadata to our database
            Document document = new Document();
            document.setFileName(originalFilename);
            document.setDocumentType(documentType);
            document.setFileUrl(fileUrl);
            document.setUploadTimestamp(LocalDateTime.now());
            document.setIssuingAuthority(issuingAuthority);
            document.setIssueDate(issueDate);
            document.setExpiryDate(expiryDate); // This was already here
            document.setRenewalPeriodDays(renewalPeriodDays);
            document.setResponsibleParty(responsibleParty);
            document.setNotes(notes);

            Document savedDocument = documentRepository.save(document);

            return convertToDto(savedDocument);

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file '" + file.getOriginalFilename() + "' to Google Cloud Storage.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> listAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateDownloadUrl(String fileName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
        URL signedUrl = storage.signUrl(blobInfo, 1, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }

    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setDocumentType(document.getDocumentType());
        dto.setFileUrl(document.getFileUrl());
        dto.setUploadTimestamp(document.getUploadTimestamp());
        dto.setIssuingAuthority(document.getIssuingAuthority());
        dto.setIssueDate(document.getIssueDate());
        dto.setExpiryDate(document.getExpiryDate());
        dto.setRenewalPeriodDays(document.getRenewalPeriodDays());
        dto.setResponsibleParty(document.getResponsibleParty());
        dto.setNotes(document.getNotes());

        return dto;
    }
}