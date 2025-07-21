package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseCategoryDto;
import com.fos.reporting.domain.ExpenseDto;
import com.fos.reporting.entity.Expense;
import com.fos.reporting.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseCategoryService expenseCategoryService) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryService = expenseCategoryService;
    }

    @Override
    @Transactional
    public ExpenseDto createExpense(ExpenseDto expenseDto) {
        Expense expense = convertToEntity(expenseDto);
        Expense savedExpense = expenseRepository.save(expense);
        return convertToDto(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDto> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getExpenseCategories() {
        // --- This now fetches dynamically from the database! ---
        return expenseCategoryService.getAllCategories().stream()
                .map(ExpenseCategoryDto::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCurrentMonthTotalExpenses() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());

        BigDecimal total = expenseRepository.sumExpensesBetweenDates(startDate, endDate);

        // If the sum is null (no expenses), return BigDecimal.ZERO
        return Optional.ofNullable(total).orElse(BigDecimal.ZERO);
    }

    // Helper methods for conversion
    private Expense convertToEntity(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setEmployeeId(dto.getEmployeeId());
        return expense;
    }

    private ExpenseDto convertToDto(Expense entity) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setCategory(entity.getCategory());
        dto.setExpenseDate(entity.getExpenseDate());
        dto.setEmployeeId(entity.getEmployeeId());
        return dto;
    }
}