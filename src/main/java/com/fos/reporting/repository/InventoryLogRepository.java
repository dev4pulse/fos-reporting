package com.fos.reporting.repository;

import com.fos.reporting.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByProductIdOrderByTransactionDateDesc(Long productId);

    Optional<InventoryLog> findTopByProductIdOrderByTransactionDateDesc(Long productId);

    /**
     * Finds the single most recent inventory log for each product.
     * This native query uses a subquery to efficiently find the latest record
     * per product_id without causing an N+1 query problem.
     *
     * @return A list of the latest InventoryLog entities.
     */
    @Query(value = "SELECT il.* FROM inventory_log il " +
            "INNER JOIN (" +
            "    SELECT product_id, MAX(transaction_date) AS max_date " +
            "    FROM inventory_log " +
            "    GROUP BY product_id" +
            ") latest ON il.product_id = latest.product_id AND il.transaction_date = latest.max_date",
            nativeQuery = true)
    List<InventoryLog> findLatestLogForEachProduct();

    List<InventoryLog> findAllByOrderByTransactionDateDesc();
}