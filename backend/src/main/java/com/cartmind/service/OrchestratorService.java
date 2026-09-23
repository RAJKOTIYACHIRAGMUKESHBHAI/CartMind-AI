package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.ProviderResult;
import com.cartmind.model.RankedProduct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrchestratorService {

    private final RequirementExtractor requirementExtractor;
    private final ProviderOrchestrator providerOrchestrator;
    private final ProductNormalizationService normalizationService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final RankingService rankingService;

    public OrchestratorService(
            RequirementExtractor requirementExtractor,
            ProviderOrchestrator providerOrchestrator,
            ProductNormalizationService normalizationService,
            DuplicateDetectionService duplicateDetectionService,
            RankingService rankingService
    ) {
        this.requirementExtractor = requirementExtractor;
        this.providerOrchestrator = providerOrchestrator;
        this.normalizationService = normalizationService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.rankingService = rankingService;
    }

    public SearchPipelineResult search(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        // ------------------------------------------
        // 1. Extract requirements
        // ------------------------------------------

        ExtractedRequirements requirements =
                requirementExtractor.extract(query);

        // ------------------------------------------
        // 2. Query providers
        // ------------------------------------------

        List<ProviderResult> providerResults =
                providerOrchestrator.search(
                        requirements
                );

        // ------------------------------------------
        // 3. Normalize provider products
        // ------------------------------------------

        List<NormalizedProduct> normalizedProducts =
                new ArrayList<>();

        for (ProviderResult providerResult
                : providerResults) {

            if (providerResult == null) {
                continue;
            }

            normalizedProducts.addAll(
                    normalizationService.normalize(
                            providerResult
                    )
            );
        }

        // ------------------------------------------
        // 4. Remove duplicates
        // ------------------------------------------

        List<NormalizedProduct> uniqueProducts =
                duplicateDetectionService.removeDuplicates(
                        normalizedProducts
                );

        // ------------------------------------------
        // 5. Rank
        // ------------------------------------------

        List<RankedProduct> rankedProducts =
                rankingService.rank(
                        uniqueProducts,
                        requirements
                );

        return new SearchPipelineResult(
                requirements,
                providerResults,
                uniqueProducts,
                rankedProducts
        );
    }

    // ==========================================
    // Pipeline Result
    // ==========================================

    public static class SearchPipelineResult {

        private final ExtractedRequirements requirements;

        private final List<ProviderResult> providerResults;

        private final List<NormalizedProduct> normalizedProducts;

        private final List<RankedProduct> rankedProducts;

        public SearchPipelineResult(
                ExtractedRequirements requirements,
                List<ProviderResult> providerResults,
                List<NormalizedProduct> normalizedProducts,
                List<RankedProduct> rankedProducts
        ) {

            this.requirements = requirements;

            this.providerResults =
                    providerResults != null
                            ? providerResults
                            : List.of();

            this.normalizedProducts =
                    normalizedProducts != null
                            ? normalizedProducts
                            : List.of();

            this.rankedProducts =
                    rankedProducts != null
                            ? rankedProducts
                            : List.of();
        }

        public ExtractedRequirements getRequirements() {
            return requirements;
        }

        public List<ProviderResult> getProviderResults() {
            return providerResults;
        }

        public List<NormalizedProduct> getNormalizedProducts() {
            return normalizedProducts;
        }

        public List<RankedProduct> getRankedProducts() {
            return rankedProducts;
        }
    }
}
