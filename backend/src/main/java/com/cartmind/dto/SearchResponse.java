package com.cartmind.dto;

import com.cartmind.model.ProviderResult;
import com.cartmind.model.RankedProduct;
import com.cartmind.model.RecommendationType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SearchResponse {

    private String query;

    private ExtractedRequirements requirements;

    private List<ProductDTO> products = new ArrayList<>();

    private List<RankedProduct> rankedProducts = new ArrayList<>();

    private List<ProviderResult> providerResults = new ArrayList<>();

    private Map<RecommendationType, RankedProduct> recommendations =
            new EnumMap<>(RecommendationType.class);

    private String message;

    public SearchResponse() {
    }

    public SearchResponse(
            String query,
            ExtractedRequirements requirements,
            List<ProductDTO> products,
            List<RankedProduct> rankedProducts,
            List<ProviderResult> providerResults,
            Map<RecommendationType, RankedProduct> recommendations,
            String message
    ) {
        this.query = query;
        this.requirements = requirements;

        this.products = products != null
                ? products
                : new ArrayList<>();

        this.rankedProducts = rankedProducts != null
                ? rankedProducts
                : new ArrayList<>();

        this.providerResults = providerResults != null
                ? providerResults
                : new ArrayList<>();

        this.recommendations = recommendations != null
                ? recommendations
                : new EnumMap<>(RecommendationType.class);

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

    public void setRequirements(
            ExtractedRequirements requirements
    ) {
        this.requirements = requirements;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(
            List<ProductDTO> products
    ) {
        this.products = products != null
                ? products
                : new ArrayList<>();
    }

    public List<RankedProduct> getRankedProducts() {
        return rankedProducts;
    }

    public void setRankedProducts(
            List<RankedProduct> rankedProducts
    ) {
        this.rankedProducts = rankedProducts != null
                ? rankedProducts
                : new ArrayList<>();
    }

    public List<ProviderResult> getProviderResults() {
        return providerResults;
    }

    public void setProviderResults(
            List<ProviderResult> providerResults
    ) {
        this.providerResults = providerResults != null
                ? providerResults
                : new ArrayList<>();
    }

    public Map<RecommendationType, RankedProduct> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            Map<RecommendationType, RankedProduct> recommendations
    ) {
        this.recommendations = recommendations != null
                ? recommendations
                : new EnumMap<>(RecommendationType.class);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}