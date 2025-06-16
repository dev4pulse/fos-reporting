package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionsDto {
    @NotNull
    private String date;
    @NotNull
    private int employeeId;
    private float cashReceived;
    private String cashReceivedStatus;
    private float phonePay;
    private String phonePayStatus;
    private float creditCard;
    private String creditCardStatus;
    private float borrowedAmount;
    private float debtRecovered;
    private String borrower;
    private float badHandling;
    private float expenses;
}
