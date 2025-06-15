package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntryProduct {

    @NotNull
    private String date;
    private List<Product> products;
    @NotNull
    private int employeeId;
}
