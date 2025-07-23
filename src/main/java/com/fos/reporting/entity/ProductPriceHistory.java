package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price_history")
@Data
@NoArgsConstructor
public class ProductPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime effectiveDate;

    private Long changedByEmployeeId;

    public ProductPriceHistory(Product product, BigDecimal price, LocalDateTime effectiveDate, Long changedByEmployeeId) {
        this.product = product;
        this.price = price;
        this.effectiveDate = effectiveDate;
        this.changedByEmployeeId = changedByEmployeeId;
    }
}