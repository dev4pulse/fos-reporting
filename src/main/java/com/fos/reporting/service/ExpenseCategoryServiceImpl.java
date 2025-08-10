package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseCategoryDto;
import com.fos.reporting.entity.ExpenseCategory;
import com.fos.reporting.repository.ExpenseCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseCategoryServiceImpl(ExpenseCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ExpenseCategoryDto createCategory(ExpenseCategoryDto categoryDto) {
        ExpenseCategory category = new ExpenseCategory();
        category.setName(categoryDto.getName());
        ExpenseCategory savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ExpenseCategoryDto convertToDto(ExpenseCategory category) {
        ExpenseCategoryDto dto = new ExpenseCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category with ID " + id + " does not exist.");
        }
        categoryRepository.deleteById(id);
    }

}