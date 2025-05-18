package com.fos.reporting.repository;

import com.fos.reporting.entity.Sales;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    Sales findTopByProductNameAndSubProductOrderByDateTimeDesc(@NotNull String productName, String subProduct);

    List<Sales> findByDateTime(@NotNull LocalDateTime datetime);
}
