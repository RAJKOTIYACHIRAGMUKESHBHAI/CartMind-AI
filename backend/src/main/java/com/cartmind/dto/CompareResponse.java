package com.cartmind.dto;

import com.cartmind.model.ComparisonResult;

public class CompareResponse {

    private ComparisonResult comparison;

    private String message;

    public CompareResponse() {
    }

    public CompareResponse(
            ComparisonResult comparison,
            String message
    ) {
        this.comparison = comparison;
        this.message = message;
    }

    public ComparisonResult getComparison() {
        return comparison;
    }

    public void setComparison(
            ComparisonResult comparison
    ) {
        this.comparison = comparison;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
