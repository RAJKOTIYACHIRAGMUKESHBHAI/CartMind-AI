package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.model.NormalizedProduct;
import com.cartmind.model.RankedProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RankingServiceTest {

    @Test
    void shouldRankProductsByRequirementMatch() {

        ExtractedRequirements requirements = new ExtractedRequirements();

        requirements.setCategory("laptop");
        requirements.setIntent("coding");
        requirements.setMaxBudget(new BigDecimal("50000"));
        requirements.setMinRamGb(16);
        requirements.setMinStorageGb(512);
        requirements.setStorageType("SSD");

        NormalizedProduct product1 = new NormalizedProduct();

        product1.setCanonicalId("P1");
        product1.setName("Coding Laptop 16GB 512GB SSD");
        product1.setBrand("DemoBrand");
        product1.setCategory("laptop");
        product1.setPrice(new BigDecimal("45999"));
        product1.setRating(new BigDecimal("4.3"));
        product1.setProvider("LOCAL_DEMO");
        product1.setAvailability("DEMO_ONLY");
        product1.setLive(false);

        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("ram", "16GB");
        attributes1.put("storage", "512GB SSD");
        attributes1.put("processor", "Demo Processor");

        product1.setAttributes(attributes1);

        NormalizedProduct product2 = new NormalizedProduct();

        product2.setCanonicalId("P2");
        product2.setName("Budget Laptop 8GB 512GB SSD");
        product2.setBrand("DemoBrand");
        product2.setCategory("laptop");
        product2.setPrice(new BigDecimal("39999"));
        product2.setRating(new BigDecimal("4.1"));
        product2.setProvider("LOCAL_DEMO");
        product2.setAvailability("DEMO_ONLY");
        product2.setLive(false);

        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("ram", "8GB");
        attributes2.put("storage", "512GB SSD");
        attributes2.put("processor", "Demo Processor");

        product2.setAttributes(attributes2);

        RankingService rankingService = new RankingService();

        List<RankedProduct> rankedProducts =
                rankingService.rank(
                        List.of(product1, product2),
                        requirements
                );

        assertNotNull(rankedProducts);
        assertEquals(2, rankedProducts.size());

        assertEquals(
                "P1",
                rankedProducts.get(0).getProduct().getCanonicalId()
        );

        assertEquals(
                "P2",
                rankedProducts.get(1).getProduct().getCanonicalId()
        );

        assertTrue(
                rankedProducts.get(0)
                        .getScoreBreakdown()
                        .getTotalScore()
                >=
                rankedProducts.get(1)
                        .getScoreBreakdown()
                        .getTotalScore()
        );
    }
}