package com.cartmind.service;

import com.cartmind.model.ComparisonResult;
import com.cartmind.model.NormalizedProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonServiceTest {

    @Test
    void shouldCompareTwoProducts() {

        // -----------------------------
        // Product 1
        // -----------------------------
        NormalizedProduct product1 = new NormalizedProduct();

        product1.setCanonicalId("P1");
        product1.setName("Laptop A");
        product1.setBrand("BrandA");
        product1.setCategory("laptop");
        product1.setPrice(new BigDecimal("45999"));
        product1.setRating(new BigDecimal("4.3"));
        product1.setProvider("LOCAL_DEMO");

        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("ram", "16GB");
        attributes1.put("storage", "512GB SSD");
        attributes1.put("processor", "Intel i5");

        product1.setAttributes(attributes1);

        // -----------------------------
        // Product 2
        // -----------------------------
        NormalizedProduct product2 = new NormalizedProduct();

        product2.setCanonicalId("P2");
        product2.setName("Laptop B");
        product2.setBrand("BrandB");
        product2.setCategory("laptop");
        product2.setPrice(new BigDecimal("49999"));
        product2.setRating(new BigDecimal("4.6"));
        product2.setProvider("OTHER_DEMO");

        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("ram", "32GB");
        attributes2.put("storage", "1TB SSD");
        attributes2.put("processor", "Intel i7");

        product2.setAttributes(attributes2);

        // -----------------------------
        // Execute comparison
        // -----------------------------
        ComparisonService comparisonService =
                new ComparisonService();

        ComparisonResult result =
                comparisonService.compare(
                        List.of(product1, product2)
                );

        // -----------------------------
        // Basic checks
        // -----------------------------
        assertNotNull(result);

        assertNotNull(result.getProducts());
        assertEquals(2, result.getProducts().size());

        // -----------------------------
        // Price comparison
        // -----------------------------
        assertNotNull(result.getPriceComparison());
        assertEquals(2, result.getPriceComparison().size());

        // -----------------------------
        // Rating comparison
        // -----------------------------
        assertNotNull(result.getRatingComparison());
        assertEquals(2, result.getRatingComparison().size());

        // -----------------------------
        // Specification comparison
        // -----------------------------
        assertNotNull(result.getSpecificationComparison());
        assertEquals(2, result.getSpecificationComparison().size());

        // -----------------------------
        // Differences
        // -----------------------------
        assertNotNull(result.getKeyDifferences());
        assertFalse(result.getKeyDifferences().isEmpty());

        // -----------------------------
        // Trade-off summary
        // -----------------------------
        assertNotNull(result.getTradeOffSummary());
        assertFalse(result.getTradeOffSummary().isBlank());
    }

    @Test
    void shouldRejectNullProducts() {

        ComparisonService comparisonService =
                new ComparisonService();

        assertThrows(
                IllegalArgumentException.class,
                () -> comparisonService.compare(null)
        );
    }

    @Test
    void shouldRejectOnlyOneProduct() {

        ComparisonService comparisonService =
                new ComparisonService();

        NormalizedProduct product =
                new NormalizedProduct();

        product.setName("Laptop A");

        assertThrows(
                IllegalArgumentException.class,
                () -> comparisonService.compare(List.of(product))
        );
    }

    @Test
    void shouldRejectMoreThanThreeProducts() {

        ComparisonService comparisonService =
                new ComparisonService();

        NormalizedProduct product1 = new NormalizedProduct();
        product1.setName("Laptop A");

        NormalizedProduct product2 = new NormalizedProduct();
        product2.setName("Laptop B");

        NormalizedProduct product3 = new NormalizedProduct();
        product3.setName("Laptop C");

        NormalizedProduct product4 = new NormalizedProduct();
        product4.setName("Laptop D");

        assertThrows(
                IllegalArgumentException.class,
                () -> comparisonService.compare(
                        List.of(
                                product1,
                                product2,
                                product3,
                                product4
                        )
                )
        );
    }
}