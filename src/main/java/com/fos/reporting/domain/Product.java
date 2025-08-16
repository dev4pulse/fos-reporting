package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
public class Product {
    private String productName;
    private String gun;
    private float opening;
    private float closing;
    private float testing;
    @NotNull
    private float price;
    private float salesInRupees;
    private BigDecimal saleInLiters;

}