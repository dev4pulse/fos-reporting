package com.fos.reporting.repository;

import com.fos.reporting.entity.Inventory;
import com.fos.reporting.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findTopByProductOrderByLastUpdatedDesc(Product product);

    List<Inventory> findByProductOrderByLastUpdatedDesc(Product product);

    @Query("SELECT DISTINCT i.product FROM Inventory i")
    List<Product> findDistinctProducts();
}
