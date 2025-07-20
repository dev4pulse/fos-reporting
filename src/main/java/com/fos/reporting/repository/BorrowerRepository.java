package com.fos.reporting.repository;

import java.util.List;

import com.fos.reporting.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    List<Borrower> findByCustomerName(String customerName);
}