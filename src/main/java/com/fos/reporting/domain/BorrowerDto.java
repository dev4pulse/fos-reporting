package com.fos.reporting.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowerDto {
    private String customerName;
    private float amountBorrowed;
}
