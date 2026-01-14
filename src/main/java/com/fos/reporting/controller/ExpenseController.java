package com.fos.reporting.controller;

import com.fos.reporting.domain.ExpenseDto;
import com.fos.reporting.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * POST /api/expenses : Creates a new expense record.
     */
    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        log.info("Creating new expense: {}", expenseDto);
        ExpenseDto createdExpense = expenseService.createExpense(expenseDto);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    /**
     * GET /api/expenses : Gets a list of all expenses.
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        log.info("Fetching all expenses");
        List<ExpenseDto> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * PUT /api/expenses/{id} : Updates an expense by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDto expenseDto) {
        log.info("Updating expense with id: {}", id);
        ExpenseDto updatedExpense = expenseService.updateExpense(id, expenseDto);
        return ResponseEntity.ok(updatedExpense);
    }

    /**
     * DELETE /api/expenses/{id} : Deletes an expense by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        log.info("Deleting expense with id: {}", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/expenses/total/current-month : Gets total expenses for the current month.
     */
    @GetMapping("/total/current-month")
    public ResponseEntity<Map<String, BigDecimal>> getCurrentMonthTotalExpenses() {
        log.info("Calculating current month total expenses");
        BigDecimal total = expenseService.calculateCurrentMonthTotalExpenses();
        return ResponseEntity.ok(Collections.singletonMap("total", total));
    }
}
