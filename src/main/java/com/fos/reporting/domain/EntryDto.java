package com.fos.reporting.domain;

import lombok.Data;

import java.util.List;

@Data
public class EntryDto {
    private String date;
    private int employeeId;

    // sales
    private List<Product> products;

    // collections
    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private List<BorrowerDto> borrowers;
}
