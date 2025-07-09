package com.fos.reporting.repository;

import com.fos.reporting.entity.Sales;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    Sales findTopByProductNameAndGunOrderByDateTimeDesc(@NotNull String productName, String gun);

    List<Sales> findByDateTime(@NotNull LocalDateTime datetime);

    List<Sales> findByDateTimeBetween(@NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate);
}
