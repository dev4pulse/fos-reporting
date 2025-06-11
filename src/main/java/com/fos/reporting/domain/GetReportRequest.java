package com.fos.reporting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetReportRequest {
    @NotNull
    private String fromDate;
    @NotNull
    private String toDate;

}
