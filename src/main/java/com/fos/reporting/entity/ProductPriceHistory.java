package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "changed_by_employee_id", nullable = false)
    private Long changedByEmployeeId;

    public ProductPriceHistory(Product product, BigDecimal price, LocalDateTime effectiveDate, Long changedByEmployeeId) {
        this.product = product;
        this.price = price;
        this.effectiveDate = effectiveDate;
        this.changedByEmployeeId = changedByEmployeeId;
    }
}
