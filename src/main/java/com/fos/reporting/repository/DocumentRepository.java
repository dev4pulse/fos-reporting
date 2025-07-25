package com.fos.reporting.repository;

import com.fos.reporting.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByOrderByUploadTimestampDesc();

    Optional<Document> findByBlobName(String blobName);
}