package com.fos.reporting.repository;

import com.fos.reporting.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findTopByProductNameIgnoreCaseOrderByLastUpdatedDesc(String productName);

    @Query("SELECT DISTINCT LOWER(i.productName) FROM Inventory i")
    List<String> findDistinctProductNames();

}
