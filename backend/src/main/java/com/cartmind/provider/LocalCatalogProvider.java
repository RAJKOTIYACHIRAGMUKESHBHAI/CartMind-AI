package com.cartmind.provider;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.dto.ProductDTO;
import com.cartmind.model.ProviderResult;
import com.cartmind.service.ProductSearchService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LocalCatalogProvider implements CommerceProvider {

    private final ProductSearchService productSearchService;

    public LocalCatalogProvider(
            ProductSearchService productSearchService
    ) {
        this.productSearchService = productSearchService;
    }

    @Override
    public String getProviderName() {
        return "LOCAL_DEMO";
    }

    @Override
    public boolean isLive() {
        return false;
    }

    @Override
    public ProviderResult search(
            ExtractedRequirements requirements
    ) {

        try {
            List<ProductDTO> products =
                    productSearchService.search(requirements);

            return new ProviderResult(
                    getProviderName(),
                    false,
                    "DEMO",
                    Instant.now(),
                    products,
                    null
            );

        } catch (Exception e) {

            return new ProviderResult(
                    getProviderName(),
                    false,
                    "ERROR",
                    Instant.now(),
                    List.of(),
                    e.getMessage()
            );
        }
    }
}