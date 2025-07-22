package com.fos.reporting.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpenseCategoryDto {
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    private String name;
}
