package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @NotNull
    private String productName;
    private String subProduct;
    private float closing;
    @NotNull
    private float price;
    private float testing;
}
