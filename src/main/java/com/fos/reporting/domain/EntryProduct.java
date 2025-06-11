package com.fos.reporting.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryProduct {

    /** Binds "YYYY-MM-DD" JSON strings into a LocalDate */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    /** Employee ID from the form */
    @NotNull
    private Integer employeeId;

    /** List of products (petrol/diesel/etc.) */
    @NotNull
    private List<Product> products;
}

