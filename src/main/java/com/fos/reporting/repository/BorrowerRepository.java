package com.fos.reporting.repository;

import java.util.List;

import com.fos.reporting.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    /**
     * Finds all borrower records for a given customer name, ignoring case,
     * and sorts them by borrowDate in descending order (most recent first).
     */
    List<Borrower> findByCustomerNameContainingIgnoreCaseOrderByBorrowDateDesc(String customerName);
}