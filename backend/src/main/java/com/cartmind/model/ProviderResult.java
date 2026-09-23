package com.cartmind.model;

import com.cartmind.dto.ProductDTO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ProviderResult {

    private String provider;

    private boolean live;

    private String status;

    private Instant fetchedAt;

    private List<ProductDTO> products = new ArrayList<>();

    private String errorMessage;

    public ProviderResult() {
    }

    public ProviderResult(
            String provider,
            boolean live,
            String status,
            Instant fetchedAt,
            List<ProductDTO> products,
            String errorMessage
    ) {
        this.provider = provider;
        this.live = live;
        this.status = status;
        this.fetchedAt = fetchedAt;
        this.products = products != null
                ? products
                : new ArrayList<>();
        this.errorMessage = errorMessage;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products != null
                ? products
                : new ArrayList<>();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
