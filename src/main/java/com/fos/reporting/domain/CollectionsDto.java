package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionsDto {
    @NotNull
    private String date;

    @NotNull
    private int employeeId;

    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private float borrowedAmount;
    private float debtRecovered;
    private float shortCollections;

    // ✅ NEW: List of borrowers instead of single string
    private List<BorrowerDto> borrowers;
}

