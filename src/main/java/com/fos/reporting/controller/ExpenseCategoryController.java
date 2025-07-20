package com.fos.reporting.controller;

import com.fos.reporting.domain.ExpenseCategoryDto;
import com.fos.reporting.service.ExpenseCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseCategoryController {

    private final ExpenseCategoryService categoryService;

    public ExpenseCategoryController(ExpenseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * POST /api/expense-categories : Creates a new expense category.
     * @param categoryDto The category to create.
     * @return The created category with its new ID.
     */
    @PostMapping("/categoryPost")
    public ResponseEntity<ExpenseCategoryDto> createCategory(@RequestBody @Valid ExpenseCategoryDto categoryDto) {
        ExpenseCategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * GET /api/expense-categories : Gets a list of all available expense categories.
     * @return A list of all categories.
     */
    @GetMapping("/categoryList")
    public ResponseEntity<List<ExpenseCategoryDto>> getAllCategories() {
        List<ExpenseCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}