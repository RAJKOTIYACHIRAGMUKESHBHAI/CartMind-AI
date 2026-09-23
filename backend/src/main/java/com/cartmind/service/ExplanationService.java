package com.cartmind.service;

import com.cartmind.entity.Product;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.prompt.ExplanationPrompt;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExplanationService {

    private final BedrockRuntimeClient bedrockRuntimeClient;

    public ExplanationService(
            BedrockRuntimeClient bedrockRuntimeClient
    ) {
        this.bedrockRuntimeClient = bedrockRuntimeClient;
    }

    // ==========================================
    // Explanation using NormalizedProduct
    // ==========================================

    public String explain(
            NormalizedProduct product,
            String userQuery
    ) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null"
            );
        }

        if (userQuery == null || userQuery.isBlank()) {
            throw new IllegalArgumentException(
                    "User query cannot be empty"
            );
        }

        try {

            String prompt =
                    ExplanationPrompt.build(
                            product,
                            userQuery
                    );

            Message message = Message.builder()
                    .role(ConversationRole.USER)
                    .content(
                            ContentBlock.fromText(prompt)
                    )
                    .build();

            String modelId =
                    "global.anthropic.claude-haiku-4-5-20251001-v1:0";

            ConverseRequest request =
                    ConverseRequest.builder()
                            .modelId(modelId)
                            .messages(message)
                            .inferenceConfig(
                                    config -> config
                                            .maxTokens(400)
                                            .temperature(0.0F)
                            )
                            .build();

            ConverseResponse response =
                    bedrockRuntimeClient.converse(
                            request
                    );

            if (response.output() == null
                    || response.output().message() == null
                    || response.output().message().content() == null
                    || response.output().message().content().isEmpty()) {

                return buildFallbackExplanation(product);
            }

            String explanation =
                    response.output()
                            .message()
                            .content()
                            .get(0)
                            .text();

            if (explanation == null
                    || explanation.isBlank()) {

                return buildFallbackExplanation(product);
            }

            return explanation.trim();

        } catch (SdkException e) {

            return buildFallbackExplanation(product);
        }
    }

    // ==========================================
    // Explanation using database Product
    // ==========================================

    public String explain(
            Product product,
            String userQuery
    ) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null"
            );
        }

        NormalizedProduct normalizedProduct =
                convertToNormalizedProduct(product);

        return explain(
                normalizedProduct,
                userQuery
        );
    }

    // ==========================================
    // Product -> NormalizedProduct
    // ==========================================

    private NormalizedProduct convertToNormalizedProduct(
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

        /*
         * Database catalog is not treated as verified
         * live provider data.
         */
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
    // Attributes parser
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

        String result =
                value.trim();

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

    // ==========================================
    // Fallback explanation
    // ==========================================

    private String buildFallbackExplanation(
            NormalizedProduct product
    ) {

        StringBuilder explanation =
                new StringBuilder();

        explanation.append(
                "Why this product: "
        );

        boolean added = false;

        if (product.getPrice() != null) {

            explanation.append(
                    "listed price is ₹"
            );

            explanation.append(
                    product.getPrice()
                            .toPlainString()
            );

            added = true;
        }

        if (product.getRating() != null) {

            if (added) {
                explanation.append("; ");
            }

            explanation.append(
                    "rating is "
            );

            explanation.append(
                    product.getRating()
                            .toPlainString()
            );

            added = true;
        }

        if (product.getAvailability() != null
                && !product.getAvailability().isBlank()) {

            if (added) {
                explanation.append("; ");
            }

            explanation.append(
                    "availability is "
            );

            explanation.append(
                    product.getAvailability()
            );

            added = true;
        }

        Map<String, String> attributes =
                product.getAttributes();

        if (attributes != null
                && !attributes.isEmpty()) {

            if (added) {
                explanation.append("; ");
            }

            explanation.append(
                    "available specifications include "
            );

            explanation.append(
                    attributes
            );

            added = true;
        }

        if (!added) {

            explanation.append(
                    "no additional verified product facts are available."
            );
        }

        return explanation.toString();
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}