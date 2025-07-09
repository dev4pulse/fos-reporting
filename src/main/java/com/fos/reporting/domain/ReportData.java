package com.fos.reporting.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportData {
    private float saleInLtr;
    private float expectedCollections;
}
