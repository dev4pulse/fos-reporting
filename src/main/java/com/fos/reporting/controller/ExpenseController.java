package com.fos.reporting.controller;

import com.fos.reporting.domain.ExpenseDto;
import com.fos.reporting.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * POST /api/expenses : Creates a new expense record.
     * @param expenseDto The expense to create.
     * @return The created expense with its new ID.
     */
    @PostMapping("/expensesPost")
    public ResponseEntity<ExpenseDto> createExpense(@RequestBody @Valid ExpenseDto expenseDto) {
        ExpenseDto createdExpense = expenseService.createExpense(expenseDto);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    /**
     * GET /api/expenses : Gets a list of all expenses.
     * @return A list of all recorded expenses.
     */
    @GetMapping("/expensesList")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        List<ExpenseDto> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/total/current-month")
    public ResponseEntity<Map<String, BigDecimal>> getCurrentMonthTotalExpenses() {
        BigDecimal total = expenseService.calculateCurrentMonthTotalExpenses();
        // Returning a JSON object is good practice for frontend clients
        return ResponseEntity.ok(Collections.singletonMap("total", total));
    }
}