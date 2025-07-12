package com.fos.reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrowers")
@Data
public class Borrower {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private float amount;
    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;
    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collections collection;
}