package com.cartmind.controller;

import com.cartmind.dto.CompareRequest;
import com.cartmind.dto.CompareResponse;
import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.dto.ProductDTO;
import com.cartmind.dto.SearchRequest;
import com.cartmind.dto.SearchResponse;
import com.cartmind.entity.Product;
import com.cartmind.model.ComparisonResult;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.ProviderResult;
import com.cartmind.model.RankedProduct;
import com.cartmind.model.RecommendationType;
import com.cartmind.repository.ProductRepository;
import com.cartmind.service.ComparisonService;
import com.cartmind.service.ExplanationService;
import com.cartmind.service.OrchestratorService;
import com.cartmind.service.RecommendationService;
import com.cartmind.service.RequirementExtractor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiSearchController {

    private final RequirementExtractor requirementExtractor;
    private final OrchestratorService orchestratorService;
    private final RecommendationService recommendationService;
    private final ExplanationService explanationService;
    private final ProductRepository productRepository;
    private final ComparisonService comparisonService;

    public AiSearchController(
            RequirementExtractor requirementExtractor,
            OrchestratorService orchestratorService,
            RecommendationService recommendationService,
            ExplanationService explanationService,
            ProductRepository productRepository,
            ComparisonService comparisonService
    ) {
        this.requirementExtractor = requirementExtractor;
        this.orchestratorService = orchestratorService;
        this.recommendationService = recommendationService;
        this.explanationService = explanationService;
        this.productRepository = productRepository;
        this.comparisonService = comparisonService;
    }

    // ==========================================
    // Requirement Extraction
    // ==========================================

    @PostMapping("/extract")
    public ResponseEntity<ExtractedRequirements> extractRequirements(
            @Valid @RequestBody SearchRequest request
    ) {

        ExtractedRequirements requirements =
                requirementExtractor.extract(
                        request.getQuery()
                );

        return ResponseEntity.ok(requirements);
    }

    // ==========================================
    // Complete CartMind Search Pipeline
    // ==========================================

    @PostMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @Valid @RequestBody SearchRequest request
    ) {

        OrchestratorService.SearchPipelineResult result =
                orchestratorService.search(
                        request.getQuery()
                );

        List<ProductDTO> products =
                new ArrayList<>();

        for (ProviderResult providerResult
                : result.getProviderResults()) {

            if (providerResult == null
                    || providerResult.getProducts() == null) {
                continue;
            }

            products.addAll(
                    providerResult.getProducts()
            );
        }

        List<RankedProduct> rankedProducts =
                result.getRankedProducts();

        List<ProviderResult> providerResults =
                result.getProviderResults();

        Map<RecommendationType, RankedProduct> recommendations =
                recommendationService.buildRecommendations(
                        rankedProducts
                );

        String message =
                rankedProducts.isEmpty()
                        ? "No matching products found."
                        : "Matching products found.";

        SearchResponse response =
                new SearchResponse(
                        request.getQuery(),
                        result.getRequirements(),
                        products,
                        rankedProducts,
                        providerResults,
                        recommendations,
                        message
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // Why This?
    // ==========================================

    @PostMapping("/explain")
    public ResponseEntity<String> explain(
            @Valid @RequestBody com.cartmind.dto.ExplanationRequest request
    ) {

        Product product =
                productRepository.findById(
                        request.getProductId()
                ).orElse(null);

        if (product == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found.");
        }

        String explanation =
                explanationService.explain(
                        product,
                        request.getQuery()
                );

        return ResponseEntity.ok(explanation);
    }

    // ==========================================
    // Compare 2-3 Products
    // ==========================================

    @PostMapping("/compare")
    public ResponseEntity<CompareResponse> compare(
            @Valid @RequestBody CompareRequest request
    ) {

        // ------------------------------------------
        // 1. Get requested product IDs
        // ------------------------------------------

        List<Long> productIds =
                request.getProductIds();

        if (productIds == null
                || productIds.size() < 2
                || productIds.size() > 3) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new CompareResponse(
                                    null,
                                    "You can compare 2 to 3 products."
                            )
                    );
        }

        // ------------------------------------------
        // 2. Fetch products from database
        // ------------------------------------------

        List<NormalizedProduct> normalizedProducts =
                new ArrayList<>();

        for (Long productId : productIds) {

            if (productId == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                new CompareResponse(
                                        null,
                                        "Product ID cannot be null."
                                )
                        );
            }

            Product product =
                    productRepository.findById(
                            productId
                    ).orElse(null);

            if (product == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                new CompareResponse(
                                        null,
                                        "Product not found: "
                                                + productId
                                )
                        );
            }

            normalizedProducts.add(
                    toNormalizedProduct(product)
            );
        }

        // ------------------------------------------
        // 3. Generate comparison
        // ------------------------------------------

        ComparisonResult comparison =
                comparisonService.compare(
                        normalizedProducts
                );

        // ------------------------------------------
        // 4. Final response
        // ------------------------------------------

        CompareResponse response =
                new CompareResponse(
                        comparison,
                        "Comparison generated successfully."
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // Product -> NormalizedProduct
    // ==========================================

    private NormalizedProduct toNormalizedProduct(
            Product product
    ) {

        NormalizedProduct normalized =
                new NormalizedProduct();

        normalized.setCanonicalId(
                "database:" + product.getId()
        );

        normalized.setName(
                safe(product.getName())
        );

        normalized.setBrand(
                safe(product.getBrand())
        );

        normalized.setCategory(
                safe(product.getCategory())
        );

        normalized.setPrice(
                product.getPrice()
        );

        normalized.setRating(
                product.getRating()
        );

        normalized.setDescription(
                safe(product.getDescription())
        );

        normalized.setImageUrl(
                safe(product.getImageUrl())
        );

        normalized.setProductUrl(
                safe(product.getProductUrl())
        );

        normalized.setProvider(
                safe(product.getProvider())
        );

        normalized.setAvailability(
                safe(product.getAvailability())
        );

        // Current local database catalog is not live.
        normalized.setLive(false);

        normalized.setFetchedAt(
                Instant.now()
        );

        normalized.setExternalProductId(
                safe(product.getExternalProductId())
        );

        normalized.setAttributes(
                parseAttributes(
                        product.getAttributes()
                )
        );

        return normalized;
    }

    // ==========================================
    // Parse current attributes format
    // ==========================================

    private Map<String, String> parseAttributes(
            String attributes
    ) {

        Map<String, String> result =
                new LinkedHashMap<>();

        if (attributes == null
                || attributes.isBlank()) {

            return result;
        }

        String cleaned =
                attributes
                        .replace("{", "")
                        .replace("}", "")
                        .trim();

        if (cleaned.isBlank()) {
            return result;
        }

        String[] pairs =
                cleaned.split(",");

        for (String pair : pairs) {

            String[] keyValue =
                    pair.split(":", 2);

            if (keyValue.length != 2) {
                continue;
            }

            String key =
                    stripQuotes(
                            keyValue[0].trim()
                    );

            String value =
                    stripQuotes(
                            keyValue[1].trim()
                    );

            if (!key.isBlank()) {
                result.put(key, value);
            }
        }

        return result;
    }

    private String stripQuotes(String value) {

        if (value == null) {
            return "";
        }

        String result = value.trim();

        if (result.startsWith("\"")
                && result.endsWith("\"")
                && result.length() >= 2) {

            result = result.substring(
                    1,
                    result.length() - 1
            );
        }

        return result;
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}