package com.fos.reporting.domain;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportData {
    private float saleInLtr;
    private float expectedCollections;
}
