package com.cartmind.provider;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.model.ProviderResult;

public interface CommerceProvider {

    /**
     * Provider ka unique name.
     *
     * Example:
     * AMAZON
     * FLIPKART
     * LOCAL_DEMO
     */
    String getProviderName();

    /**
     * Ye batata hai ki provider actual live
     * commerce data provide karta hai ya nahi.
     */
    boolean isLive();

    /**
     * Given shopping requirements ke basis par
     * products search karta hai.
     */
    ProviderResult search(ExtractedRequirements requirements);
}