package com.cartmind.dto;

import java.math.BigDecimal;

public class ProductDTO {

    private Long id;

    private String name;

    private String brand;

    private String category;

    private BigDecimal price;

    private BigDecimal rating;

    private String description;

    private String imageUrl;

    private String productUrl;

    private String provider;

    private String availability;

    private String attributes;

    private String externalProductId;

    public ProductDTO() {
    }

    public ProductDTO(
            Long id,
            String name,
            String brand,
            String category,
            BigDecimal price,
            BigDecimal rating,
            String description,
            String imageUrl,
            String productUrl,
            String provider,
            String availability,
            String attributes,
            String externalProductId
    ) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.provider = provider;
        this.availability = availability;
        this.attributes = attributes;
        this.externalProductId = externalProductId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }
}