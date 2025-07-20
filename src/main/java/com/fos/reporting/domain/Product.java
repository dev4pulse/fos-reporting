package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class Product {
    private String productName;
    private String gun;
    private float opening;
    private float closing;
    private float testing;
    @NotNull
    private float price;
}