package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class ProductDto {
    private Long productId;
    @NotNull
    private String name;
    private String description;
}
