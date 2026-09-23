package com.cartmind.service;

import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.RankedProduct;
import com.cartmind.model.RecommendationType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    /**
     * Creates recommendation buckets from already-ranked products.
     *
     * BEST_MATCH:
     * Highest deterministic ranking score.
     *
     * BEST_OVERALL:
     * Currently same as highest-ranked product.
     * Later this can include additional business rules.
     *
     * BEST_PRICE:
     * Only from live provider results.
     *
     * BEST_RATED:
     * Highest verified rating value available.
     */
    public Map<RecommendationType, RankedProduct> buildRecommendations(
            List<RankedProduct> rankedProducts
    ) {

        Map<RecommendationType, RankedProduct> recommendations =
                new EnumMap<>(RecommendationType.class);

        if (rankedProducts == null
                || rankedProducts.isEmpty()) {

            return recommendations;
        }

        // ------------------------------------------
        // Best Match
        // ------------------------------------------

        RankedProduct bestMatch =
                rankedProducts.stream()
                        .filter(this::hasProduct)
                        .max(
                                Comparator.comparingDouble(
                                        product -> product
                                                .getScoreBreakdown()
                                                .getTotalScore()
                                )
                        )
                        .orElse(null);

        if (bestMatch != null) {

            recommendations.put(
                    RecommendationType.BEST_MATCH,
                    bestMatch
            );

            recommendations.put(
                    RecommendationType.BEST_OVERALL,
                    bestMatch
            );
        }

        // ------------------------------------------
        // Best Price
        // Only verified live data
        // ------------------------------------------

        RankedProduct bestPrice =
                rankedProducts.stream()
                        .filter(this::hasProduct)
                        .filter(this::isLiveProduct)
                        .filter(this::hasPrice)
                        .min(
                                Comparator.comparing(
                                        product -> product
                                                .getProduct()
                                                .getPrice()
                                )
                        )
                        .orElse(null);

        if (bestPrice != null) {

            recommendations.put(
                    RecommendationType.BEST_PRICE,
                    bestPrice
            );
        }

        // ------------------------------------------
        // Best Rated
        // ------------------------------------------

        RankedProduct bestRated =
                rankedProducts.stream()
                        .filter(this::hasProduct)
                        .filter(this::hasRating)
                        .max(
                                Comparator.comparing(
                                        product -> product
                                                .getProduct()
                                                .getRating()
                                )
                        )
                        .orElse(null);

        if (bestRated != null) {

            recommendations.put(
                    RecommendationType.BEST_RATED,
                    bestRated
            );
        }

        return recommendations;
    }

    private boolean hasProduct(
            RankedProduct rankedProduct
    ) {

        return rankedProduct != null
                && rankedProduct.getProduct() != null
                && rankedProduct.getScoreBreakdown() != null;
    }

    private boolean isLiveProduct(
            RankedProduct rankedProduct
    ) {

        NormalizedProduct product =
                rankedProduct.getProduct();

        return product.isLive();
    }

    private boolean hasPrice(
            RankedProduct rankedProduct
    ) {

        NormalizedProduct product =
                rankedProduct.getProduct();

        BigDecimal price =
                product.getPrice();

        return price != null
                && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean hasRating(
            RankedProduct rankedProduct
    ) {

        return rankedProduct
                .getProduct()
                .getRating() != null;
    }
}
