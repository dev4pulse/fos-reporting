package com.fos.reporting.repository;

import com.fos.reporting.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // You can add methods for custom queries here later, e.g., find by category or date range.
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    BigDecimal sumExpensesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}