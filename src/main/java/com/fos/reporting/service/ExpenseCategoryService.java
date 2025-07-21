package com.fos.reporting.service;

import com.fos.reporting.domain.ExpenseCategoryDto;
import java.util.List;

public interface ExpenseCategoryService {
    ExpenseCategoryDto createCategory(ExpenseCategoryDto categoryDto);
    List<ExpenseCategoryDto> getAllCategories();
}