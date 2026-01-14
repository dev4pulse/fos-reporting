package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionsDto {
    @NotNull
    private String date; // yyyy-MM-dd HH:mm:ss
    @NotNull
    private Long employeeId;
    private float cashReceived;
    private float phonePay;
    private float creditCard;
    private float shortCollections;
}
