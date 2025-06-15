package com.fos.reporting.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String subProduct;
    private float opening;
    private float closing;
    private float price;
    @Column(nullable = true)
    private float literssold;
    @Column(nullable = true)
    private float revenue;
    @Column(nullable = true)
    private float testing;


}
