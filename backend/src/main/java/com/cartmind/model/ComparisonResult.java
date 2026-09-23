package com.cartmind.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComparisonResult {

    private List<NormalizedProduct> products = new ArrayList<>();

    private Map<String, String> priceComparison;

    private Map<String, String> ratingComparison;

    private Map<String, String> specificationComparison;

    private List<String> keyDifferences = new ArrayList<>();

    private String tradeOffSummary;

    public ComparisonResult() {
    }

    public ComparisonResult(
            List<NormalizedProduct> products,
            Map<String, String> priceComparison,
            Map<String, String> ratingComparison,
            Map<String, String> specificationComparison,
            List<String> keyDifferences,
            String tradeOffSummary
    ) {
        this.products = products != null
                ? products
                : new ArrayList<>();

        this.priceComparison = priceComparison;

        this.ratingComparison = ratingComparison;

        this.specificationComparison =
                specificationComparison;

        this.keyDifferences = keyDifferences != null
                ? keyDifferences
                : new ArrayList<>();

        this.tradeOffSummary = tradeOffSummary;
    }

    public List<NormalizedProduct> getProducts() {
        return products;
    }

    public void setProducts(
            List<NormalizedProduct> products
    ) {
        this.products = products != null
                ? products
                : new ArrayList<>();
    }

    public Map<String, String> getPriceComparison() {
        return priceComparison;
    }

    public void setPriceComparison(
            Map<String, String> priceComparison
    ) {
        this.priceComparison = priceComparison;
    }

    public Map<String, String> getRatingComparison() {
        return ratingComparison;
    }

    public void setRatingComparison(
            Map<String, String> ratingComparison
    ) {
        this.ratingComparison = ratingComparison;
    }

    public Map<String, String> getSpecificationComparison() {
        return specificationComparison;
    }

    public void setSpecificationComparison(
            Map<String, String> specificationComparison
    ) {
        this.specificationComparison =
                specificationComparison;
    }

    public List<String> getKeyDifferences() {
        return keyDifferences;
    }

    public void setKeyDifferences(
            List<String> keyDifferences
    ) {
        this.keyDifferences = keyDifferences != null
                ? keyDifferences
                : new ArrayList<>();
    }

    public String getTradeOffSummary() {
        return tradeOffSummary;
    }

    public void setTradeOffSummary(
            String tradeOffSummary
    ) {
        this.tradeOffSummary = tradeOffSummary;
    }
}