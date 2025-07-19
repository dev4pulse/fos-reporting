package com.fos.reporting.domain;

import lombok.Data;

import java.util.List;

@Data // combination of sales and collections
public class EntryDto {
    private String date;
    private Long employeeId;
    private List<Product> products;
    private float cashReceived;
    private float phonePay;
    private float creditCard;
}