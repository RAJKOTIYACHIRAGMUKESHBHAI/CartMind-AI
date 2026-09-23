package com.cartmind.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String category;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(length = 5000)
    private String description;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 1000)
    private String productUrl;

    @Column(length = 100)
    private String provider;

    @Column(length = 100)
    private String availability;

    @Column(length = 1000)
    private String attributes;

    @Column(length = 100)
    private String externalProductId;
}