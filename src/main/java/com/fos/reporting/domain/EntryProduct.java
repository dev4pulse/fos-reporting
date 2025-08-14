// src/main/java/com/fos/reporting/domain/EntryProduct.java
package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EntryProduct {
    private String date;
    @NotNull
    private Long employeeId;
    private List<Product> products;
    private String entryId; // Lombok generates getter/setter
}