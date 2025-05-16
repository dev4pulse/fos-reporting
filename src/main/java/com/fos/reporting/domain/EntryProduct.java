package com.fos.reporting.domain;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntryProduct {

    private String date;
    private List<Product> products;
    private int employeeId;
}
