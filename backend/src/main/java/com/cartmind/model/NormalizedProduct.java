package com.cartmind.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class NormalizedProduct {

    private String canonicalId;

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

    private boolean live;

    private Instant fetchedAt;

    private String externalProductId;

    private Map<String, String> attributes = new LinkedHashMap<>();

    public NormalizedProduct() {
    }

    public String getCanonicalId() {
        return canonicalId;
    }

    public void setCanonicalId(String canonicalId) {
        this.canonicalId = canonicalId;
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

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes != null
                ? attributes
                : new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        return "NormalizedProduct{" +
                "canonicalId='" + canonicalId + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", provider='" + provider + '\'' +
                ", live=" + live +
                ", externalProductId='" + externalProductId + '\'' +
                '}';
    }
}
