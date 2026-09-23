package com.cartmind.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CompareRequest {

    @NotEmpty(message = "At least 2 product IDs are required")
    @Size(
            min = 2,
            max = 3,
            message = "You can compare 2 to 3 products"
    )
    private List<Long> productIds = new ArrayList<>();

    public CompareRequest() {
    }

    public CompareRequest(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds != null
                ? productIds
                : new ArrayList<>();
    }
}

