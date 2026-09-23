package com.cartmind.model;

public class RankedProduct {

    private NormalizedProduct product;

    private ScoreBreakdown scoreBreakdown;

    private String recommendation;

    public RankedProduct() {
    }

    public RankedProduct(
            NormalizedProduct product,
            ScoreBreakdown scoreBreakdown,
            String recommendation
    ) {
        this.product = product;
        this.scoreBreakdown = scoreBreakdown;
        this.recommendation = recommendation;
    }

    public NormalizedProduct getProduct() {
        return product;
    }

    public void setProduct(NormalizedProduct product) {
        this.product = product;
    }

    public ScoreBreakdown getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}