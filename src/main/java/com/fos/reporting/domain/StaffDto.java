package com.fos.reporting.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String role;
    private String status;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String timeAtWork;
    private LocalDate joinedDate;
    private LocalDate date;
}