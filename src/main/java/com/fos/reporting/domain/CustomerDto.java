package com.fos.reporting.domain;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data           // generates getters/setters for every field
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {

    private Long customerId;          // ← now declared

    @NotNull
    private String customerFirstName;

    @NotNull
    private String customerLastName;

    @Email
    private String customerEmail;

    @NotNull
    private int customerPhoneNumber;

    private String vehicleNumber;
}
