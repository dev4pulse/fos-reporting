package com.fos.reporting.repository;

import com.fos.reporting.entity.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionsRepository extends JpaRepository<Collections, Long> {
    List<Collections> findByDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
    void deleteByEntryId(String entryId);
    List<Collections> findByEntryId(String entryId);

    // Add this method to get the latest collection by ID
    Optional<Collections> findTopByOrderByIdDesc();
}