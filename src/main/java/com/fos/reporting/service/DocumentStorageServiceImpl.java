package com.fos.reporting.service;

import com.fos.reporting.domain.DocumentDto;
import com.fos.reporting.entity.Document;
import com.fos.reporting.repository.DocumentRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private final Storage storage;
    private final DocumentRepository documentRepository;
    private final String bucketName;

    public DocumentStorageServiceImpl(Storage storage, // Injected by Spring Cloud GCP
                                      DocumentRepository documentRepository,
                                      @Value("${gcs.bucket.name}") String bucketName) {
        this.storage = storage;
        this.documentRepository = documentRepository;
        this.bucketName = bucketName;
    }

    @Override
    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, String documentType, LocalDate expiryDate) {
        try {
            // 1. Generate a unique blob name for the file in GCS
            String blobName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // 2. Configure the blob for upload
            BlobId blobId = BlobId.of(bucketName, blobName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            // 3. Perform the upload to GCS
            storage.create(blobInfo, file.getBytes());

            // 4. Construct the public URL of the uploaded file
            // IMPORTANT: This requires your GCS bucket to be publicly accessible.
            String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, blobName);

            // 5. Create and save the document metadata to our database
            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setDocumentType(documentType);
            document.setFileUrl(fileUrl);
            document.setExpiryDate(expiryDate);
            document.setUploadTimestamp(LocalDateTime.now());

            Document savedDocument = documentRepository.save(document);

            return convertToDto(savedDocument);

        } catch (IOException e) {
            // You can use your custom exception handler here
            throw new RuntimeException("Failed to upload file to Google Cloud Storage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> listAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setDocumentType(document.getDocumentType());
        dto.setFileUrl(document.getFileUrl());
        dto.setExpiryDate(document.getExpiryDate());
        dto.setUploadTimestamp(document.getUploadTimestamp());
        return dto;
    }
}