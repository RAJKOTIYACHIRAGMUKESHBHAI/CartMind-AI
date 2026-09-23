package com.cartmind.service;

import com.cartmind.model.ComparisonResult;
import com.cartmind.model.NormalizedProduct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ComparisonService {

    public ComparisonResult compare(
            List<NormalizedProduct> products
    ) {

        validateProducts(products);

        List<NormalizedProduct> cleanProducts =
                new ArrayList<>();

        for (NormalizedProduct product : products) {
            if (product != null) {
                cleanProducts.add(product);
            }
        }

        Map<String, String> priceComparison =
                buildPriceComparison(cleanProducts);

        Map<String, String> ratingComparison =
                buildRatingComparison(cleanProducts);

        Map<String, String> specificationComparison =
                buildSpecificationComparison(cleanProducts);

        List<String> keyDifferences =
                buildKeyDifferences(cleanProducts);

        String tradeOffSummary =
                buildTradeOffSummary(cleanProducts);

        return new ComparisonResult(
                cleanProducts,
                priceComparison,
                ratingComparison,
                specificationComparison,
                keyDifferences,
                tradeOffSummary
        );
    }

    // ==========================================
    // Validation
    // ==========================================

    private void validateProducts(
            List<NormalizedProduct> products
    ) {

        if (products == null) {
            throw new IllegalArgumentException(
                    "Products cannot be null"
            );
        }

        int validCount = 0;

        for (NormalizedProduct product : products) {
            if (product != null) {
                validCount++;
            }
        }

        if (validCount < 2 || validCount > 3) {
            throw new IllegalArgumentException(
                    "You can compare between 2 and 3 products"
            );
        }
    }

    // ==========================================
    // Price Comparison
    // ==========================================

    private Map<String, String> buildPriceComparison(
            List<NormalizedProduct> products
    ) {

        Map<String, String> result =
                new LinkedHashMap<>();

        for (NormalizedProduct product : products) {

            String label =
                    buildProductLabel(product);

            String price =
                    formatPrice(product.getPrice());

            result.put(label, price);
        }

        return result;
    }

    // ==========================================
    // Rating Comparison
    // ==========================================

    private Map<String, String> buildRatingComparison(
            List<NormalizedProduct> products
    ) {

        Map<String, String> result =
                new LinkedHashMap<>();

        for (NormalizedProduct product : products) {

            String label =
                    buildProductLabel(product);

            String rating =
                    product.getRating() != null
                            ? product.getRating().toPlainString()
                            : "Unavailable";

            result.put(label, rating);
        }

        return result;
    }

    // ==========================================
    // Specification Comparison
    // ==========================================

    private Map<String, String> buildSpecificationComparison(
            List<NormalizedProduct> products
    ) {

        Map<String, String> result =
                new LinkedHashMap<>();

        for (NormalizedProduct product : products) {

            String label =
                    buildProductLabel(product);

            String specifications =
                    formatSpecifications(
                            product.getAttributes()
                    );

            result.put(
                    label,
                    specifications
            );
        }

        return result;
    }

    // ==========================================
    // Key Differences
    // ==========================================

    private List<String> buildKeyDifferences(
            List<NormalizedProduct> products
    ) {

        List<String> differences =
                new ArrayList<>();

        addPriceDifference(
                products,
                differences
        );

        addRatingDifference(
                products,
                differences
        );

        addSpecificationDifferences(
                products,
                differences
        );

        addProviderDifference(
                products,
                differences
        );

        return differences;
    }

    // ==========================================
    // Price Difference
    // ==========================================

    private void addPriceDifference(
            List<NormalizedProduct> products,
            List<String> differences
    ) {

        List<NormalizedProduct> withPrice =
                products.stream()
                        .filter(product ->
                                product.getPrice() != null)
                        .sorted(
                                Comparator.comparing(
                                        NormalizedProduct::getPrice
                                )
                        )
                        .toList();

        if (withPrice.size() < 2) {
            return;
        }

        NormalizedProduct lower =
                withPrice.get(0);

        NormalizedProduct higher =
                withPrice.get(withPrice.size() - 1);

        if (lower.getPrice().compareTo(
                higher.getPrice()
        ) != 0) {

            BigDecimal difference =
                    higher.getPrice()
                            .subtract(
                                    lower.getPrice()
                            );

            differences.add(
                    buildProductLabel(lower)
                            + " has a lower listed price than "
                            + buildProductLabel(higher)
                            + " by ₹"
                            + difference.toPlainString()
            );
        }
    }

    // ==========================================
    // Rating Difference
    // ==========================================

    private void addRatingDifference(
            List<NormalizedProduct> products,
            List<String> differences
    ) {

        List<NormalizedProduct> withRating =
                products.stream()
                        .filter(product ->
                                product.getRating() != null)
                        .sorted(
                                Comparator.comparing(
                                        NormalizedProduct::getRating
                                )
                        )
                        .toList();

        if (withRating.size() < 2) {
            return;
        }

        NormalizedProduct lower =
                withRating.get(0);

        NormalizedProduct higher =
                withRating.get(
                        withRating.size() - 1
                );

        if (lower.getRating().compareTo(
                higher.getRating()
        ) != 0) {

            differences.add(
                    buildProductLabel(higher)
                            + " has a higher listed rating ("
                            + higher.getRating().toPlainString()
                            + ") than "
                            + buildProductLabel(lower)
                            + " ("
                            + lower.getRating().toPlainString()
                            + ")"
            );
        }
    }

    // ==========================================
    // Specification Differences
    // ==========================================

    private void addSpecificationDifferences(
            List<NormalizedProduct> products,
            List<String> differences
    ) {

        Map<String, List<String>> valuesByKey =
                new LinkedHashMap<>();

        for (NormalizedProduct product : products) {

            Map<String, String> attributes =
                    product.getAttributes();

            if (attributes == null) {
                continue;
            }

            for (Map.Entry<String, String> entry
                    : attributes.entrySet()) {

                String key =
                        normalizeKey(entry.getKey());

                String value =
                        entry.getValue();

                if (value == null) {
                    continue;
                }

                valuesByKey
                        .computeIfAbsent(
                                key,
                                ignored -> new ArrayList<>()
                        )
                        .add(value);
            }
        }

        for (Map.Entry<String, List<String>> entry
                : valuesByKey.entrySet()) {

            List<String> values =
                    entry.getValue();

            boolean different = false;

            String firstValue =
                    values.get(0);

            for (String value : values) {

                if (!firstValue.equalsIgnoreCase(value)) {
                    different = true;
                    break;
                }
            }

            if (different) {

                differences.add(
                        "Specification difference for "
                                + entry.getKey()
                                + ": "
                                + String.join(
                                        " | ",
                                        values
                                )
                );
            }
        }
    }

    // ==========================================
    // Provider Difference
    // ==========================================

    private void addProviderDifference(
            List<NormalizedProduct> products,
            List<String> differences
    ) {

        String firstProvider =
                products.get(0).getProvider();

        for (NormalizedProduct product : products) {

            if (!safe(firstProvider)
                    .equalsIgnoreCase(
                            safe(product.getProvider())
                    )) {

                differences.add(
                        "Products are listed by different providers."
                );

                return;
            }
        }
    }

    // ==========================================
    // Trade-off Summary
    // ==========================================

    private String buildTradeOffSummary(
            List<NormalizedProduct> products
    ) {

        if (products.size() < 2) {
            return "Not enough products to describe trade-offs.";
        }

        NormalizedProduct lowerPrice =
                products.stream()
                        .filter(product ->
                                product.getPrice() != null)
                        .min(
                                Comparator.comparing(
                                        NormalizedProduct::getPrice
                                )
                        )
                        .orElse(null);

        NormalizedProduct higherRating =
                products.stream()
                        .filter(product ->
                                product.getRating() != null)
                        .max(
                                Comparator.comparing(
                                        NormalizedProduct::getRating
                                )
                        )
                        .orElse(null);

        if (lowerPrice != null
                && higherRating != null) {

            boolean sameProduct =
                    lowerPrice == higherRating;

            if (!sameProduct) {

                return buildProductLabel(lowerPrice)
                        + " has the lower listed price, while "
                        + buildProductLabel(higherRating)
                        + " has the higher listed rating among the provided data.";
            }
        }

        if (lowerPrice != null) {

            return "The provided products have different listed prices; "
                    + "review their specifications and ratings together.";
        }

        if (higherRating != null) {

            return "The provided products have different listed ratings; "
                    + "review their prices and specifications together.";
        }

        return "Review the available specifications and provider information "
                + "to understand the trade-offs.";
    }

    // ==========================================
    // Formatting
    // ==========================================

    private String formatSpecifications(
            Map<String, String> attributes
    ) {

        if (attributes == null
                || attributes.isEmpty()) {

            return "Unavailable";
        }

        List<String> values =
                new ArrayList<>();

        for (Map.Entry<String, String> entry
                : attributes.entrySet()) {

            values.add(
                    entry.getKey()
                            + "="
                            + safe(entry.getValue())
            );
        }

        return String.join(
                ", ",
                values
        );
    }

    private String formatPrice(
            BigDecimal price
    ) {

        if (price == null) {
            return "Unavailable";
        }

        return "₹" + price.toPlainString();
    }

    private String buildProductLabel(
            NormalizedProduct product
    ) {

        String name =
                safe(product.getName());

        String provider =
                safe(product.getProvider());

        if (provider.isBlank()) {
            return name;
        }

        if (name.isBlank()) {
            return provider;
        }

        return name + " [" + provider + "]";
    }

    private String normalizeKey(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}