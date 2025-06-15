package com.fos.reporting.domain;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetReportResponse {

    private float actualCollection;
    private float difference;
    private ReportData petrol;
    private ReportData diesel;
}
