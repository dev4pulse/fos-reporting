package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class GetReportRequest {
    @NotNull
    private String fromDate;
    @NotNull
    private String toDate;
}