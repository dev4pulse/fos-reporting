package com.fos.reporting.repository;

import com.fos.reporting.entity.Collections;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CollectionsRepository extends JpaRepository<Collections, Long> {

    List<Collections> findByDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);

}
