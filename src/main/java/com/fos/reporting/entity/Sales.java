

package com.fos.reporting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sales {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private String productName;
    private String subProduct;
    @NotNull
    private int employeeId;
    private float openingStock;
    private float closingStock;
    private float price;
    @Column(nullable = true)
    private float testing;
    @Column(nullable = true)
    private float literssold;
    @Column(nullable = true)
    private float revenue;
    private float saleAmount;
}
