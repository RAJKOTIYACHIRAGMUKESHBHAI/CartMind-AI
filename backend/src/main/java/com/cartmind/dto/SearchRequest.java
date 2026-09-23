package com.cartmind.dto;

import jakarta.validation.constraints.NotBlank;

public class SearchRequest {

    @NotBlank(message = "Search query cannot be empty")
    private String query;

    public SearchRequest() {
    }

    public SearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}