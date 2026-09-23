package com.cartmind.provider;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.dto.ProductDTO;
import com.cartmind.model.ProviderResult;
import com.cartmind.service.ProductSearchService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalCatalogProviderTest {

    @Test
    void shouldReturnDemoProviderResult() {

        ProductSearchService productSearchService =
                mock(ProductSearchService.class);

        LocalCatalogProvider provider =
                new LocalCatalogProvider(productSearchService);

        ExtractedRequirements requirements =
                new ExtractedRequirements();

        ProductDTO product =
                mock(ProductDTO.class);

        when(productSearchService.search(requirements))
                .thenReturn(List.of(product));

        ProviderResult result =
                provider.search(requirements);

        assertNotNull(result);

        assertEquals(
                "LOCAL_DEMO",
                result.getProvider()
        );

        assertFalse(result.isLive());

        assertEquals(
                "DEMO",
                result.getStatus()
        );

        assertNotNull(result.getFetchedAt());

        assertNotNull(result.getProducts());

        assertEquals(
                1,
                result.getProducts().size()
        );

        assertNull(result.getErrorMessage());

        verify(productSearchService, times(1))
                .search(requirements);
    }

    @Test
    void shouldReturnErrorResultWhenSearchFails() {

        ProductSearchService productSearchService =
                mock(ProductSearchService.class);

        LocalCatalogProvider provider =
                new LocalCatalogProvider(productSearchService);

        ExtractedRequirements requirements =
                new ExtractedRequirements();

        when(productSearchService.search(requirements))
                .thenThrow(
                        new RuntimeException("Database error")
                );

        ProviderResult result =
                provider.search(requirements);

        assertNotNull(result);

        assertEquals(
                "LOCAL_DEMO",
                result.getProvider()
        );

        assertFalse(result.isLive());

        assertEquals(
                "ERROR",
                result.getStatus()
        );

        assertNotNull(result.getFetchedAt());

        assertNotNull(result.getProducts());

        assertTrue(
                result.getProducts().isEmpty()
        );

        assertEquals(
                "Database error",
                result.getErrorMessage()
        );
    }
}