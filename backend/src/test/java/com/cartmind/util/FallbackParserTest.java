package com.cartmind.util;

import com.cartmind.dto.ExtractedRequirements;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FallbackParserTest {

    @Test
    void shouldParseStandardLaptopRequirements() {

        String query =
                "50000 ke andar coding ke liye 16GB RAM aur 512GB SSD laptop chahiye";

        ExtractedRequirements result =
                FallbackParser.parse(query);

        assertNotNull(result);

        assertEquals(
                "laptop",
                result.getCategory()
        );

        assertEquals(
                new BigDecimal("50000"),
                result.getMaxBudget()
        );

        assertEquals(
                16,
                result.getMinRamGb()
        );

        assertEquals(
                512,
                result.getMinStorageGb()
        );

        assertEquals(
                "SSD",
                result.getStorageType()
        );
    }


    @Test
    void shouldParseAnderBudgetPhrase() {

        String query =
                "5000 ke ander laptop chahiye";

        ExtractedRequirements result =
                FallbackParser.parse(query);

        assertNotNull(result);

        assertEquals(
                new BigDecimal("5000"),
                result.getMaxBudget()
        );

    }


    @Test
    void shouldParseUnderBudgetPhrase() {

        String query =
                "laptop under 5000";

        ExtractedRequirements result =
                FallbackParser.parse(query);

        assertNotNull(result);

        assertEquals(
                new BigDecimal("5000"),
                result.getMaxBudget()
        );

    }


    @Test
    void shouldParseRupeeBudgetPhrase() {

        String query =
                "₹5000 ke andar laptop chahiye";

        ExtractedRequirements result =
                FallbackParser.parse(query);

        assertNotNull(result);

        assertEquals(
                new BigDecimal("5000"),
                result.getMaxBudget()
        );

    }
}