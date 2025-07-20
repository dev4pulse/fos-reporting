package com.fos.reporting.domain;

import lombok.Data;

@Data
public class UpdatePriceDto {
    private String productName;
    private float newPrice;
}
