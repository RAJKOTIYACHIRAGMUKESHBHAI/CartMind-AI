package com.cartmind.service;

import com.cartmind.model.NormalizedProduct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class DuplicateDetectionService {

    /**
     * Removes duplicate records while preserving products
     * coming from different providers.
     */
    public List<NormalizedProduct> removeDuplicates(
            List<NormalizedProduct> products
    ) {

        if (products == null || products.isEmpty()) {
            return List.of();
        }

        List<NormalizedProduct> uniqueProducts =
                new ArrayList<>();

        Set<String> seenKeys = new HashSet<>();

        for (NormalizedProduct product : products) {

            if (product == null) {
                continue;
            }

            String duplicateKey =
                    buildDuplicateKey(product);

            if (seenKeys.add(duplicateKey)) {
                uniqueProducts.add(product);
            }
        }

        return uniqueProducts;
    }

    /**
     * Creates a safe duplicate key.
     *
     * Priority:
     *
     * 1. provider + externalProductId
     * 2. provider + brand + category + name + price
     */
    private String buildDuplicateKey(
            NormalizedProduct product
    ) {

        String provider =
                normalize(product.getProvider());

        String externalId =
                normalize(product.getExternalProductId());

        // ------------------------------------------
        // Strong identifier
        // ------------------------------------------

        if (!externalId.isBlank()) {

            return "ID|"
                    + provider
                    + "|"
                    + externalId;
        }

        // ------------------------------------------
        // Safe fallback key
        // ------------------------------------------

        String brand =
                normalize(product.getBrand());

        String category =
                normalize(product.getCategory());

        String name =
                normalize(product.getName());

        String price =
                normalizePrice(product.getPrice());

        return "PRODUCT|"
                + provider
                + "|"
                + brand
                + "|"
                + category
                + "|"
                + name
                + "|"
                + price;
    }

    /**
     * Returns true when two products look like the same
     * physical product across providers.
     *
     * This method does NOT automatically merge them.
     * It is intentionally conservative.
     */
    public boolean areLikelySameProduct(
            NormalizedProduct first,
            NormalizedProduct second
    ) {

        if (first == null || second == null) {
            return false;
        }

        // Same provider + same external ID
        if (sameText(
                first.getProvider(),
                second.getProvider()
        )
                && sameText(
                first.getExternalProductId(),
                second.getExternalProductId()
        )
                && !isBlank(first.getExternalProductId())) {

            return true;
        }

        // Brand must match when both are available
        if (!isBlank(first.getBrand())
                && !isBlank(second.getBrand())
                && !sameText(
                first.getBrand(),
                second.getBrand()
        )) {

            return false;
        }

        // Category must match when both are available
        if (!isBlank(first.getCategory())
                && !isBlank(second.getCategory())
                && !sameText(
                first.getCategory(),
                second.getCategory()
        )) {

            return false;
        }

        // Normalized product names must match exactly
        String firstName =
                normalize(first.getName());

        String secondName =
                normalize(second.getName());

        if (firstName.isBlank()
                || secondName.isBlank()) {

            return false;
        }

        return firstName.equals(secondName);
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizePrice(BigDecimal price) {

        if (price == null) {
            return "";
        }

        return price.stripTrailingZeros()
                .toPlainString();
    }

    private boolean sameText(
            String first,
            String second
    ) {

        if (isBlank(first) || isBlank(second)) {
            return false;
        }

        return normalize(first)
                .equals(normalize(second));
    }

    private boolean isBlank(String value) {

        return value == null || value.isBlank();
    }
}