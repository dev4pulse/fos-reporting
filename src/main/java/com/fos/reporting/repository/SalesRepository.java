package com.fos.reporting.repository;

import com.fos.reporting.entity.Sales;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    Sales findTopByProductNameAndGunOrderByDateTimeDesc(String productName, String gun);

    List<Sales> findByDateTime(LocalDateTime dateTime);

    List<Sales> findByDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
}