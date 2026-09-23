package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.dto.ProductDTO;
import com.cartmind.entity.Product;
import com.cartmind.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductSearchService {

    private final ProductRepository productRepository;

    public ProductSearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> search(ExtractedRequirements requirements) {

        if (requirements == null) {
            throw new IllegalArgumentException(
                    "Search requirements cannot be null"
            );
        }

        List<Product> products = getCandidateProducts(requirements);

        List<ProductDTO> results = new ArrayList<>();

        for (Product product : products) {

            if (!matchesRequirements(product, requirements)) {
                continue;
            }

            results.add(toDTO(product));
        }

        return results;
    }

    private List<Product> getCandidateProducts(
            ExtractedRequirements requirements
    ) {

        String category = requirements.getCategory();
        BigDecimal maxBudget = requirements.getMaxBudget();

        // Category + budget
        if (category != null
                && !category.isBlank()
                && maxBudget != null) {

            return productRepository
                    .findByCategoryIgnoreCaseAndPriceLessThanEqual(
                            category,
                            maxBudget
                    );
        }

        // Category only
        if (category != null && !category.isBlank()) {
            return productRepository.findByCategoryIgnoreCase(
                    category
            );
        }

        // Budget only
        if (maxBudget != null) {
            return productRepository.findByPriceLessThanEqual(
                    maxBudget
            );
        }

        // No database filter
        return productRepository.findAll();
    }

    private boolean matchesRequirements(
            Product product,
            ExtractedRequirements requirements
    ) {

        // ==========================================
        // Brand
        // ==========================================

        if (requirements.getBrands() != null
                && !requirements.getBrands().isEmpty()) {

            if (product.getBrand() == null) {
                return false;
            }

            boolean brandMatches = false;

            for (String requestedBrand : requirements.getBrands()) {

                if (product.getBrand()
                        .equalsIgnoreCase(requestedBrand)) {

                    brandMatches = true;
                    break;
                }
            }

            if (!brandMatches) {
                return false;
            }
        }

        // ==========================================
        // Rating
        // ==========================================

        if (requirements.getMinRating() != null) {

            if (product.getRating() == null) {
                return false;
            }

            if (product.getRating().doubleValue()
                    < requirements.getMinRating()) {

                return false;
            }
        }

        // ==========================================
        // RAM from attributes
        // ==========================================

        if (requirements.getMinRamGb() != null) {

            Integer productRamGb =
                    extractRamGb(product.getAttributes());

            if (productRamGb == null
                    || productRamGb < requirements.getMinRamGb()) {

                return false;
            }
        }

        // ==========================================
        // Storage from attributes
        // ==========================================

        if (requirements.getMinStorageGb() != null) {

            Integer productStorageGb =
                    extractStorageGb(product.getAttributes());

            if (productStorageGb == null
                    || productStorageGb < requirements.getMinStorageGb()) {

                return false;
            }
        }

        // ==========================================
        // Storage type from attributes
        // ==========================================

        if (requirements.getStorageType() != null
                && !requirements.getStorageType().isBlank()) {

            String attributes = product.getAttributes();

            if (attributes == null || attributes.isBlank()) {
                return false;
            }

            String lowerAttributes =
                    attributes.toLowerCase(Locale.ROOT);

            String requiredType =
                    requirements.getStorageType()
                            .toLowerCase(Locale.ROOT);

            if (!lowerAttributes.contains(requiredType)) {
                return false;
            }
        }

        return true;
    }

    // ==========================================
    // Extract RAM from attributes
    // Examples:
    // {"ram":"16GB"}
    // 16GB RAM
    // RAM: 16GB
    // ==========================================

    private Integer extractRamGb(String attributes) {

        if (attributes == null || attributes.isBlank()) {
            return null;
        }

        String lower =
                attributes.toLowerCase(Locale.ROOT);

        String[] patterns = {
                "\"ram\"\\s*:\\s*\"?(\\d+)\\s*gb?",
                "(\\d+)\\s*gb\\s*ram",
                "ram\\s*[:=]?\\s*(\\d+)\\s*gb?"
        };

        for (String regex : patterns) {

            Pattern pattern = Pattern.compile(
                    regex,
                    Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = pattern.matcher(lower);

            if (matcher.find()) {
                return Integer.parseInt(
                        matcher.group(1)
                );
            }
        }

        return null;
    }

    // ==========================================
    // Extract storage from attributes
    // Examples:
    // {"storage":"512GB SSD"}
    // 512GB SSD
    // 1TB SSD
    // ==========================================

    private Integer extractStorageGb(String attributes) {

        if (attributes == null || attributes.isBlank()) {
            return null;
        }

        String lower =
                attributes.toLowerCase(Locale.ROOT);

        Pattern pattern = Pattern.compile(
                "(\\d+)\\s*(gb|tb)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(lower);

        while (matcher.find()) {

            int value =
                    Integer.parseInt(matcher.group(1));

            String unit =
                    matcher.group(2)
                            .toLowerCase(Locale.ROOT);

            if ("tb".equals(unit)) {
                value = value * 1024;
            }

            // Ignore values that are unlikely to be storage
            // such as small numbers.
            if (value >= 64) {
                return value;
            }
        }

        return null;
    }

    // ==========================================
    // Entity -> DTO
    // ==========================================

    private ProductDTO toDTO(Product product) {

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getPrice(),
                product.getRating(),
                product.getDescription(),
                product.getImageUrl(),
                product.getProductUrl(),
                product.getProvider(),
                product.getAvailability(),
                product.getAttributes(),
                product.getExternalProductId()
        );
    }
}