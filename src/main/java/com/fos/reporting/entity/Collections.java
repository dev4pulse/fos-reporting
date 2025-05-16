package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collections")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Collections {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private LocalDateTime dateTime;
    private int employeeId;
    private int cash;
    private int phonePay;
    private int cc;
    private int debt;
    private int badHandling;
    private int amountUsed;
}
