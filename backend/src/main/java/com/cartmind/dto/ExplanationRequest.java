package com.cartmind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExplanationRequest {

    @NotBlank(message = "Query cannot be empty")
    private String query;

    @NotNull(message = "Product ID is required")
    private Long productId;

    public ExplanationRequest() {
    }

    public ExplanationRequest(
            String query,
            Long productId
    ) {
        this.query = query;
        this.productId = productId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
