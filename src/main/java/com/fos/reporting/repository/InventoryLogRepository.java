package com.fos.reporting.repository;

import com.fos.reporting.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Import Optional

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByProductIdOrderByTransactionDateDesc(Long productId);

    Optional<InventoryLog> findTopByProductIdOrderByTransactionDateDesc(Long productId);
}