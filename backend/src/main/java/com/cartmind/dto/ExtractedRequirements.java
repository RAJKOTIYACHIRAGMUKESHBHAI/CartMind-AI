package com.cartmind.dto;

import java.math.BigDecimal;
import java.util.List;

public class ExtractedRequirements {

    private String category;

    private String intent;

    private BigDecimal maxBudget;

    private BigDecimal minBudget;

    private List<String> brands;

    private Integer minRamGb;

    private Integer minStorageGb;

    private String storageType;

    private Integer minBatteryMah;

    private String processor;

    private String operatingSystem;

    private Double minRating;

    private String language;

    private String location;

    private List<String> preferredFeatures;

    public ExtractedRequirements() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public BigDecimal getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }

    public BigDecimal getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(BigDecimal minBudget) {
        this.minBudget = minBudget;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public Integer getMinRamGb() {
        return minRamGb;
    }

    public void setMinRamGb(Integer minRamGb) {
        this.minRamGb = minRamGb;
    }

    public Integer getMinStorageGb() {
        return minStorageGb;
    }

    public void setMinStorageGb(Integer minStorageGb) {
        this.minStorageGb = minStorageGb;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Integer getMinBatteryMah() {
        return minBatteryMah;
    }

    public void setMinBatteryMah(Integer minBatteryMah) {
        this.minBatteryMah = minBatteryMah;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getPreferredFeatures() {
        return preferredFeatures;
    }

    public void setPreferredFeatures(List<String> preferredFeatures) {
        this.preferredFeatures = preferredFeatures;
    }
}
