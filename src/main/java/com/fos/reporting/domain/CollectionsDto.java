package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
public class CollectionsDto {
    @NotNull
    private String date; // Expecting format: yyyy-MM-dd HH:mm:ss
    @NotNull
    private Integer employeeId;
    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private float borrowedAmount;
    private float shortCollections;

    private List<BorrowerDto> borrowers;
}