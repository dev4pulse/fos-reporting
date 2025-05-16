package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Sales {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private LocalDateTime dateTime;
    private int productId;
    private int employeeId;
    private int openingStock;
    private int closingStock;
    private int testingTotal;
    private int sale;
    private int cost;
    private int inventory;
}
