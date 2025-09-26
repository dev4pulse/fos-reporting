package com.fos.reporting.repository;

import com.fos.reporting.entity.ProfitLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitLossRepository extends JpaRepository<ProfitLoss, Long> {
}
