package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.RankedProduct;
import com.cartmind.model.ScoreBreakdown;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RankingService {

    // ==========================================
    // CartMind Ranking Weights
    // ==========================================

    private static final double REQUIREMENT_WEIGHT = 40.0;
    private static final double BUDGET_WEIGHT = 25.0;
    private static final double SPECIFICATION_WEIGHT = 20.0;
    private static final double QUALITY_WEIGHT = 15.0;

    // ==========================================
    // Main Ranking Method
    // ==========================================

    public List<RankedProduct> rank(
            List<NormalizedProduct> products,
            ExtractedRequirements requirements
    ) {

        if (products == null || products.isEmpty()) {
            return List.of();
        }

        if (requirements == null) {
            throw new IllegalArgumentException(
                    "Search requirements cannot be null"
            );
        }

        List<RankedProduct> rankedProducts =
                new ArrayList<>();

        for (NormalizedProduct product : products) {

            if (product == null) {
                continue;
            }

            ScoreBreakdown breakdown =
                    calculateScore(
                            product,
                            requirements
                    );

            String recommendation =
                    getRecommendationLabel(
                            breakdown.getTotalScore()
                    );

            rankedProducts.add(
                    new RankedProduct(
                            product,
                            breakdown,
                            recommendation
                    )
            );
        }

        // Highest score first
        rankedProducts.sort(
                Comparator.comparingDouble(
                        (RankedProduct rankedProduct) ->
                                rankedProduct
                                        .getScoreBreakdown()
                                        .getTotalScore()
                ).reversed()
        );

        return rankedProducts;
    }

    // ==========================================
    // Total Score
    // ==========================================

    private ScoreBreakdown calculateScore(
            NormalizedProduct product,
            ExtractedRequirements requirements
    ) {

        double requirementMatch =
                calculateRequirementMatch(
                        product,
                        requirements
                );

        double budgetFit =
                calculateBudgetFit(
                        product,
                        requirements
                );

        double specificationMatch =
                calculateSpecificationMatch(
                        product,
                        requirements
                );

        double qualitySignals =
                calculateQualitySignals(product);

        double totalScore =
                requirementMatch
                        + budgetFit
                        + specificationMatch
                        + qualitySignals;

        return new ScoreBreakdown(
                round(requirementMatch),
                round(budgetFit),
                round(specificationMatch),
                round(qualitySignals),
                round(totalScore)
        );
    }

    // ==========================================
    // Requirement Match - 40 Points
    // ==========================================

    private double calculateRequirementMatch(
            NormalizedProduct product,
            ExtractedRequirements requirements
    ) {

        int checks = 0;
        int matched = 0;

        // Category
        if (!isBlank(requirements.getCategory())) {

            checks++;

            if (!isBlank(product.getCategory())
                    && product.getCategory()
                    .equalsIgnoreCase(
                            requirements.getCategory()
                    )) {

                matched++;
            }
        }

        // Intent
        if (!isBlank(requirements.getIntent())) {

            checks++;

            if (matchesIntent(
                    product,
                    requirements.getIntent()
            )) {

                matched++;
            }
        }

        // Brand
        if (requirements.getBrands() != null
                && !requirements.getBrands().isEmpty()) {

            checks++;

            if (matchesBrand(
                    product,
                    requirements.getBrands()
            )) {

                matched++;
            }
        }

        // No requirement checks
        if (checks == 0) {
            return REQUIREMENT_WEIGHT;
        }

        return ((double) matched / checks)
                * REQUIREMENT_WEIGHT;
    }

    // ==========================================
    // Budget Fit - 25 Points
    // ==========================================

    private double calculateBudgetFit(
            NormalizedProduct product,
            ExtractedRequirements requirements
    ) {

        BigDecimal price =
                product.getPrice();

        if (price == null) {
            return 0.0;
        }

        BigDecimal maxBudget =
                requirements.getMaxBudget();

        BigDecimal minBudget =
                requirements.getMinBudget();

        // ------------------------------------------
        // Maximum Budget
        // ------------------------------------------

        if (maxBudget != null) {

            if (maxBudget.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                return 0.0;
            }

            // Product above maximum budget
            if (price.compareTo(maxBudget) > 0) {
                return 0.0;
            }

            /*
             * Lower price within budget gets
             * a better budget-fit score.
             */
            double ratio =
                    price.doubleValue()
                            / maxBudget.doubleValue();

            double score =
                    BUDGET_WEIGHT
                            * (1.0 - (ratio * 0.5));

            return clamp(
                    score,
                    0.0,
                    BUDGET_WEIGHT
            );
        }

        // ------------------------------------------
        // Minimum Budget
        // ------------------------------------------

        if (minBudget != null) {

            if (price.compareTo(minBudget) < 0) {
                return 0.0;
            }

            return BUDGET_WEIGHT;
        }

        // No budget requirement
        return BUDGET_WEIGHT;
    }

    // ==========================================
    // Specification Match - 20 Points
    // ==========================================

    private double calculateSpecificationMatch(
            NormalizedProduct product,
            ExtractedRequirements requirements
    ) {

        int checks = 0;
        int matched = 0;

        // RAM
        if (requirements.getMinRamGb() != null) {

            checks++;

            Integer productRam =
                    getAttributeInteger(
                            product,
                            "ram"
                    );

            if (productRam != null
                    && productRam
                    >= requirements.getMinRamGb()) {

                matched++;
            }
        }

        // Storage
        if (requirements.getMinStorageGb() != null) {

            checks++;

            Integer productStorage =
                    getStorageGb(product);

            if (productStorage != null
                    && productStorage
                    >= requirements.getMinStorageGb()) {

                matched++;
            }
        }

        // Storage type
        if (!isBlank(
                requirements.getStorageType()
        )) {

            checks++;

            if (containsAttributeValue(
                    product,
                    requirements.getStorageType()
            )) {

                matched++;
            }
        }

        // Battery
        if (requirements.getMinBatteryMah() != null) {

            checks++;

            Integer battery =
                    getAttributeInteger(
                            product,
                            "battery"
                    );

            if (battery != null
                    && battery
                    >= requirements.getMinBatteryMah()) {

                matched++;
            }
        }

        // Processor
        if (!isBlank(
                requirements.getProcessor()
        )) {

            checks++;

            if (containsAttributeValue(
                    product,
                    requirements.getProcessor()
            )) {

                matched++;
            }
        }

        // Operating System
        if (!isBlank(
                requirements.getOperatingSystem()
        )) {

            checks++;

            if (containsAttributeValue(
                    product,
                    requirements.getOperatingSystem()
            )) {

                matched++;
            }
        }

        if (checks == 0) {
            return SPECIFICATION_WEIGHT;
        }

        return ((double) matched / checks)
                * SPECIFICATION_WEIGHT;
    }

    // ==========================================
    // Quality Signals - 15 Points
    // ==========================================

    private double calculateQualitySignals(
            NormalizedProduct product
    ) {

        double score = 0.0;

        // Rating = up to 10 points
        if (product.getRating() != null) {

            double rating =
                    product.getRating().doubleValue();

            double ratingScore =
                    (rating / 5.0) * 10.0;

            score += clamp(
                    ratingScore,
                    0.0,
                    10.0
            );
        }

        // Availability = up to 5 points
        if (isAvailable(product)) {
            score += 5.0;
        }

        return clamp(
                score,
                0.0,
                QUALITY_WEIGHT
        );
    }

    // ==========================================
    // Recommendation Label
    // ==========================================

    private String getRecommendationLabel(
            double totalScore
    ) {

        if (totalScore >= 90.0) {
            return "Excellent Match";
        }

        if (totalScore >= 75.0) {
            return "Strong Match";
        }

        if (totalScore >= 60.0) {
            return "Good Match";
        }

        return "Possible Match";
    }

    // ==========================================
    // RAM Attribute
    // ==========================================

    private Integer getAttributeInteger(
            NormalizedProduct product,
            String key
    ) {

        Map<String, String> attributes =
                product.getAttributes();

        if (attributes == null
                || attributes.isEmpty()) {

            return null;
        }

        for (Map.Entry<String, String> entry
                : attributes.entrySet()) {

            if (!entry.getKey()
                    .equalsIgnoreCase(key)) {

                continue;
            }

            String value = entry.getValue();

            if (value == null
                    || value.isBlank()) {

                return null;
            }

            Matcher matcher =
                    Pattern.compile(
                            "(\\d+)"
                    ).matcher(value);

            if (matcher.find()) {

                return Integer.parseInt(
                        matcher.group(1)
                );
            }
        }

        return null;
    }

    // ==========================================
    // Storage Attribute
    // ==========================================

    private Integer getStorageGb(
            NormalizedProduct product
    ) {

        Map<String, String> attributes =
                product.getAttributes();

        if (attributes == null
                || attributes.isEmpty()) {

            return null;
        }

        for (Map.Entry<String, String> entry
                : attributes.entrySet()) {

            if (!entry.getKey()
                    .equalsIgnoreCase("storage")) {

                continue;
            }

            String value = entry.getValue();

            if (value == null
                    || value.isBlank()) {

                return null;
            }

            Matcher matcher =
                    Pattern.compile(
                            "(\\d+)\\s*(gb|tb)",
                            Pattern.CASE_INSENSITIVE
                    ).matcher(value);

            if (!matcher.find()) {
                return null;
            }

            int number =
                    Integer.parseInt(
                            matcher.group(1)
                    );

            String unit =
                    matcher.group(2)
                            .toLowerCase(
                                    Locale.ROOT
                            );

            if ("tb".equals(unit)) {
                number *= 1024;
            }

            return number;
        }

        return null;
    }

    // ==========================================
    // Attribute Value Matching
    // ==========================================

    private boolean containsAttributeValue(
            NormalizedProduct product,
            String requestedValue
    ) {

        if (isBlank(requestedValue)) {
            return false;
        }

        Map<String, String> attributes =
                product.getAttributes();

        if (attributes == null
                || attributes.isEmpty()) {

            return false;
        }

        String requested =
                requestedValue
                        .toLowerCase(Locale.ROOT)
                        .trim();

        for (String value : attributes.values()) {

            if (value == null) {
                continue;
            }

            if (value
                    .toLowerCase(Locale.ROOT)
                    .contains(requested)) {

                return true;
            }
        }

        return false;
    }

    // ==========================================
    // Brand Matching
    // ==========================================

    private boolean matchesBrand(
            NormalizedProduct product,
            List<String> brands
    ) {

        if (isBlank(product.getBrand())) {
            return false;
        }

        for (String brand : brands) {

            if (!isBlank(brand)
                    && product.getBrand()
                    .equalsIgnoreCase(brand)) {

                return true;
            }
        }

        return false;
    }

    // ==========================================
    // Intent Matching
    // ==========================================

    private boolean matchesIntent(
            NormalizedProduct product,
            String intent
    ) {

        if (isBlank(intent)) {
            return true;
        }

        String lowerIntent =
                intent.toLowerCase(Locale.ROOT);

        /*
         * NormalizedProduct.attributes is a
         * Map<String,String>, not a String.
         *
         * We convert the map to text here.
         */
        String attributeText =
                attributesToText(
                        product.getAttributes()
                );

        String text =
                (
                        safe(product.getName())
                                + " "
                                + safe(product.getDescription())
                                + " "
                                + attributeText
                ).toLowerCase(Locale.ROOT);

        return text.contains(lowerIntent);
    }

    // ==========================================
    // Map -> String
    // ==========================================

    private String attributesToText(
            Map<String, String> attributes
    ) {

        if (attributes == null
                || attributes.isEmpty()) {

            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        for (Map.Entry<String, String> entry
                : attributes.entrySet()) {

            builder.append(
                    safe(entry.getKey())
            );

            builder.append(" ");

            builder.append(
                    safe(entry.getValue())
            );

            builder.append(" ");
        }

        return builder.toString();
    }

    // ==========================================
    // Availability
    // ==========================================

    private boolean isAvailable(
            NormalizedProduct product
    ) {

        if (isBlank(
                product.getAvailability()
        )) {

            return false;
        }

        String availability =
                product.getAvailability()
                        .toLowerCase(Locale.ROOT);

        return availability.contains("in stock")
                || availability.contains("available")
                || availability.equals("demo_only");
    }

    // ==========================================
    // Helpers
    // ==========================================

    private double clamp(
            double value,
            double min,
            double max
    ) {

        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    private double round(double value) {

        return Math.round(value * 100.0)
                / 100.0;
    }

    private boolean isBlank(String value) {

        return value == null
                || value.isBlank();
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }
}