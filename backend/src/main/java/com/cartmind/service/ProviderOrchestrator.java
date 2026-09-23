package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.model.ProviderResult;
import com.cartmind.provider.CommerceProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProviderOrchestrator {

    private final List<CommerceProvider> providers;

    public ProviderOrchestrator(List<CommerceProvider> providers) {
        this.providers = providers != null
                ? providers
                : Collections.emptyList();
    }

    public List<ProviderResult> search(
            ExtractedRequirements requirements
    ) {

        if (requirements == null) {
            throw new IllegalArgumentException(
                    "Search requirements cannot be null"
            );
        }

        List<ProviderResult> results = new ArrayList<>();

        for (CommerceProvider provider : providers) {

            if (provider == null) {
                continue;
            }

            try {

                ProviderResult result =
                        provider.search(requirements);

                if (result != null) {
                    results.add(result);
                }

            } catch (Exception e) {

                ProviderResult failedResult =
                        new ProviderResult();

                failedResult.setProvider(
                        provider.getProviderName()
                );

                failedResult.setLive(
                        provider.isLive()
                );

                failedResult.setStatus("ERROR");

                failedResult.setProducts(
                        Collections.emptyList()
                );

                failedResult.setErrorMessage(
                        e.getMessage()
                );

                results.add(failedResult);
            }
        }

        return results;
    }
}
