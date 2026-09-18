package com.cartmind.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private String query;

    private ExtractedRequirements requirements;

    private List<ProductDTO> products = new ArrayList<>();

    private String message;

    public SearchResponse() {
    }

    public SearchResponse(
            String query,
            ExtractedRequirements requirements,
            List<ProductDTO> products,
            String message
    ) {
        this.query = query;
        this.requirements = requirements;
        this.products = products;
        this.message = message;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ExtractedRequirements getRequirements() {
        return requirements;
    }

    public void setRequirements(ExtractedRequirements requirements) {
        this.requirements = requirements;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
