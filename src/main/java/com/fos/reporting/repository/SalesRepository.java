package com.fos.reporting.repository;

import com.fos.reporting.entity.Sales;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    Sales  findTopByProductNameAndSubProductOrderByDateTimeDesc(@NotNull String productName, String subProduct);
}
