package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.dto.ProductDTO;
import com.cartmind.entity.Product;
import com.cartmind.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    void shouldFilterProductsByRequirements() {

        // ---------------------------------
        // Requirements
        // ---------------------------------
        ExtractedRequirements requirements =
                new ExtractedRequirements();

        requirements.setCategory("laptop");
        requirements.setMaxBudget(new BigDecimal("50000"));
        requirements.setBrands(List.of("DemoBrand"));
        requirements.setMinRating(4.2);
        requirements.setMinRamGb(16);
        requirements.setMinStorageGb(512);
        requirements.setStorageType("SSD");

        // ---------------------------------
        // Product 1 - should PASS
        // ---------------------------------
        Product product1 = new Product();

        product1.setId(1L);
        product1.setName("Coding Laptop 16GB 512GB SSD");
        product1.setBrand("DemoBrand");
        product1.setCategory("laptop");
        product1.setPrice(new BigDecimal("45999"));
        product1.setRating(new BigDecimal("4.3"));
        product1.setDescription("Laptop for coding");
        product1.setProvider("LOCAL_DEMO");
        product1.setAvailability("DEMO_ONLY");
        product1.setAttributes(
                "{\"ram\":\"16GB\",\"storage\":\"512GB SSD\"}"
        );
        product1.setExternalProductId("DEMO-001");

        // ---------------------------------
        // Product 2 - should FAIL
        // RAM is only 8GB
        // ---------------------------------
        Product product2 = new Product();

        product2.setId(2L);
        product2.setName("Budget Laptop 8GB 512GB SSD");
        product2.setBrand("DemoBrand");
        product2.setCategory("laptop");
        product2.setPrice(new BigDecimal("39999"));
        product2.setRating(new BigDecimal("4.5"));
        product2.setDescription("Budget laptop");
        product2.setProvider("LOCAL_DEMO");
        product2.setAvailability("DEMO_ONLY");
        product2.setAttributes(
                "{\"ram\":\"8GB\",\"storage\":\"512GB SSD\"}"
        );
        product2.setExternalProductId("DEMO-002");

        // ---------------------------------
        // Product 3 - should FAIL
        // Wrong brand
        // ---------------------------------
        Product product3 = new Product();

        product3.setId(3L);
        product3.setName("Coding Laptop 16GB 512GB SSD");
        product3.setBrand("OtherBrand");
        product3.setCategory("laptop");
        product3.setPrice(new BigDecimal("47999"));
        product3.setRating(new BigDecimal("4.6"));
        product3.setDescription("Another coding laptop");
        product3.setProvider("LOCAL_DEMO");
        product3.setAvailability("DEMO_ONLY");
        product3.setAttributes(
                "{\"ram\":\"16GB\",\"storage\":\"512GB SSD\"}"
        );
        product3.setExternalProductId("DEMO-003");

        // ---------------------------------
        // Mock repository
        // ---------------------------------
        when(
                productRepository
                        .findByCategoryIgnoreCaseAndPriceLessThanEqual(
                                "laptop",
                                new BigDecimal("50000")
                        )
        ).thenReturn(
                List.of(product1, product2, product3)
        );

        // ---------------------------------
        // Execute search
        // ---------------------------------
        List<ProductDTO> results =
                productSearchService.search(requirements);

        // ---------------------------------
        // Assertions
        // ---------------------------------

        assertNotNull(results);

        // Only product1 should pass all filters
        assertEquals(1, results.size());

        assertEquals(
                "Coding Laptop 16GB 512GB SSD",
                results.get(0).getName()
        );

        assertEquals(
                "DemoBrand",
                results.get(0).getBrand()
        );

        assertEquals(
                new BigDecimal("45999"),
                results.get(0).getPrice()
        );

        // Verify repository method was called
        verify(
                productRepository,
                times(1)
        ).findByCategoryIgnoreCaseAndPriceLessThanEqual(
                "laptop",
                new BigDecimal("50000")
        );
    }

    @Test
    void shouldRejectNullRequirements() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productSearchService.search(null)
        );
    }
}