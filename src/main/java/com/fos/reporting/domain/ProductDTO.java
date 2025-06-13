package com.fos.reporting.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String productName;
    private String subProduct;
    private float opening;
    private float closing;
    private float price;
    private float literssold;
    private float revenue;
    private float testing;
}
