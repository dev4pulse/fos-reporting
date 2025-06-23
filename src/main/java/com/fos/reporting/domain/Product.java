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
    private String gun;
    private float closing;
    private float opening;
    @NotNull
    private float price;
    private float testing;
}
