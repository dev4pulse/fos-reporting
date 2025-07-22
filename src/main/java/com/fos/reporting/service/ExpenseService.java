package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseDto;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseDto expenseDto);
    List<ExpenseDto> getAllExpenses();

    BigDecimal calculateCurrentMonthTotalExpenses();

    List<String> getExpenseCategories();
}