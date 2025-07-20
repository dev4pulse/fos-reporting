package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data // EntryProduct DTO (used for sales submission)
public class EntryProduct {
    private String date; // Expecting format: yyyy-MM-dd HH:mm:ss
    @NotNull
    private Long employeeId;
    private List<Product> products;
}