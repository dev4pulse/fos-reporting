package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class GetReportRequest {
    @NotNull
    private String fromDate; // Expecting format: yyyy-MM-dd HH:mm:ss
    @NotNull
    private String toDate;
}