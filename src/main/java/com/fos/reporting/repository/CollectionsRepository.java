package com.fos.reporting.repository;

import com.fos.reporting.entity.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CollectionsRepository extends JpaRepository<Collections, Long> {
    List<Collections> findByDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
    void deleteByEntryId(String entryId);
}