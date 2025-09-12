package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseDto;
import com.fos.reporting.entity.Expense;
import com.fos.reporting.repository.ExpenseRepository;
import com.fos.reporting.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto) {
        try {
            Expense expense = mapToEntity(expenseDto);
            Expense saved = expenseRepository.save(expense);
            log.info("Expense created successfully with ID {}", saved.getId());
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error creating expense", e);
            throw new RuntimeException("Failed to create expense", e);
        }
    }

    @Override
    public List<ExpenseDto> getAllExpenses() {
        try {
            return expenseRepository.findAll()
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching expenses", e);
            throw new RuntimeException("Failed to fetch expenses", e);
        }
    }

    @Override
    public ExpenseDto updateExpense(Long id, ExpenseDto expenseDto) {
        try {
            Optional<Expense> existingOpt = expenseRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new RuntimeException("Expense not found with id " + id);
            }

            Expense existing = existingOpt.get();
            existing.setDescription(expenseDto.getDescription());
            existing.setAmount(expenseDto.getAmount());
            existing.setCategory(expenseDto.getCategory());
            existing.setExpenseDate(expenseDto.getExpenseDate());
            existing.setEmployeeId(expenseDto.getEmployeeId());

            Expense updated = expenseRepository.save(existing);
            log.info("Expense updated successfully with ID {}", updated.getId());
            return mapToDto(updated);
        } catch (Exception e) {
            log.error("Error updating expense with id {}", id, e);
            throw new RuntimeException("Failed to update expense", e);
        }
    }

    @Override
    public void deleteExpense(Long id) {
        try {
            if (!expenseRepository.existsById(id)) {
                throw new RuntimeException("Expense not found with id " + id);
            }
            expenseRepository.deleteById(id);
            log.info("Expense deleted successfully with ID {}", id);
        } catch (Exception e) {
            log.error("Error deleting expense with id {}", id, e);
            throw new RuntimeException("Failed to delete expense", e);
        }
    }

    @Override
    public BigDecimal calculateCurrentMonthTotalExpenses() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate start = now.withDayOfMonth(1);
            LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

            BigDecimal total = expenseRepository.sumExpensesBetweenDates(start, end);
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error calculating total expenses for current month", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<String> getExpenseCategories() {
        try {
            return expenseRepository.findAll()
                    .stream()
                    .map(Expense::getCategory)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching expense categories", e);
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }

    // ----------- Mapper Methods -----------
    private ExpenseDto mapToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setEmployeeId(expense.getEmployeeId());
        return dto;
    }

    private Expense mapToEntity(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setEmployeeId(dto.getEmployeeId());
        return expense;
    }
}
