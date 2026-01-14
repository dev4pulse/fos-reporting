package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseDto;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseDto expenseDto);

    List<ExpenseDto> getAllExpenses();

    ExpenseDto updateExpense(Long id, ExpenseDto expenseDto);

    void deleteExpense(Long id);

    BigDecimal calculateCurrentMonthTotalExpenses();

    List<String> getExpenseCategories();
}
