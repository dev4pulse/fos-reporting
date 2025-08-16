package com.fos.reporting.repository;

import com.fos.reporting.entity.Sales;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    Sales findTopByProductNameAndGunOrderByDateTimeDesc(String productName, String gun);

    List<Sales> findByDateTime(LocalDateTime dateTime);

    List<Sales> findByDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
    List<Sales> findTop10ByOrderByDateTimeDesc();
    void deleteByEntryId(String entryId);
    List<Sales> findByEntryId(String entryId);

    @Query("SELECT s.entryId FROM Sales s WHERE s.dateTime >= :sinceDate GROUP BY s.entryId ORDER BY MAX(s.dateTime) DESC")
    List<String> findDistinctEntryIdsByDateTimeAfter(@Param("sinceDate") LocalDateTime sinceDate);
}