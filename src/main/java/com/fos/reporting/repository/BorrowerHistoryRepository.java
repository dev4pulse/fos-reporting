package com.fos.reporting.repository;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.entity.BorrowerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowerHistoryRepository extends JpaRepository<BorrowerHistory, Long> {
    List<BorrowerHistory> findByBorrowerOrderByUpdatedAtDesc(Borrower borrower);
    List<BorrowerHistory> findByCustomerNameContainingIgnoreCaseOrderByUpdatedAtDesc(String customerName);
    Optional<BorrowerHistory> findTopByBorrowerIdOrderByUpdatedAtDesc(Long borrowerId);
}
