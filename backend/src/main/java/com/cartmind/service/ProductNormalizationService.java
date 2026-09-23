package com.cartmind.service;

import com.cartmind.dto.ProductDTO;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.ProviderResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ProductNormalizationService {

    public List<NormalizedProduct> normalize(
            ProviderResult providerResult
    ) {

        if (providerResult == null
                || providerResult.getProducts() == null) {

            return List.of();
        }

        List<NormalizedProduct> normalizedProducts =
                new ArrayList<>();

        for (ProductDTO product : providerResult.getProducts()) {

            if (product == null) {
                continue;
            }

            NormalizedProduct normalizedProduct =
                    normalizeProduct(
                            product,
                            providerResult
                    );

            normalizedProducts.add(normalizedProduct);
        }

        return normalizedProducts;
    }

    private NormalizedProduct normalizeProduct(
            ProductDTO product,
            ProviderResult providerResult
    ) {

        NormalizedProduct normalized =
                new NormalizedProduct();

        normalized.setCanonicalId(
                buildCanonicalId(product)
        );

        normalized.setName(
                cleanText(product.getName())
        );

        normalized.setBrand(
                cleanText(product.getBrand())
        );

        normalized.setCategory(
                cleanText(product.getCategory())
        );

        normalized.setPrice(
                product.getPrice()
        );

        normalized.setRating(
                product.getRating()
        );

        normalized.setDescription(
                cleanText(product.getDescription())
        );

        normalized.setImageUrl(
                cleanText(product.getImageUrl())
        );

        normalized.setProductUrl(
                cleanText(product.getProductUrl())
        );

        normalized.setProvider(
                providerResult.getProvider()
        );

        normalized.setAvailability(
                cleanText(product.getAvailability())
        );

        normalized.setLive(
                providerResult.isLive()
        );

        normalized.setFetchedAt(
                providerResult.getFetchedAt() != null
                        ? providerResult.getFetchedAt()
                        : Instant.now()
        );

        normalized.setExternalProductId(
                cleanText(product.getExternalProductId())
        );

        normalized.setAttributes(
                parseAttributes(product.getAttributes())
        );

        return normalized;
    }

    private String buildCanonicalId(ProductDTO product) {

        String externalId =
                cleanText(product.getExternalProductId());

        if (!externalId.isBlank()) {

            String provider =
                    cleanText(product.getProvider());

            return normalizeKey(provider)
                    + ":"
                    + normalizeKey(externalId);
        }

        String brand =
                normalizeKey(product.getBrand());

        String name =
                normalizeKey(product.getName());

        String category =
                normalizeKey(product.getCategory());

        return brand
                + ":"
                + category
                + ":"
                + name;
    }

    private Map<String, String> parseAttributes(
            String attributes
    ) {

        Map<String, String> result =
                new LinkedHashMap<>();

        if (attributes == null
                || attributes.isBlank()) {

            return result;
        }

        String text = attributes.trim();

        /*
         * Simple parser for the current local catalog format:
         *
         * {"ram":"16GB","storage":"512GB SSD"}
         *
         * This keeps the service dependency-light.
         * More complex provider-specific JSON can be
         * handled later without changing the normalized model.
         */

        String cleaned =
                text.replace("{", "")
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

    private String cleanText(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private String normalizeKey(String value) {

        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
