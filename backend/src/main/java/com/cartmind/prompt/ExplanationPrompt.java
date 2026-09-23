package com.cartmind.prompt;

import com.cartmind.model.NormalizedProduct;

import java.util.Map;

public final class ExplanationPrompt {

    private ExplanationPrompt() {
        // Utility class
    }

    public static String build(
            NormalizedProduct product,
            String userQuery
    ) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null"
            );
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are CartMind AI.

                Explain why the selected product matches the user's
                shopping requirements.

                STRICT TRUST RULES:
                1. Use ONLY facts provided in PRODUCT DATA.
                2. Do not invent specifications.
                3. Do not invent price, rating, availability,
                   discounts, reviews, or performance claims.
                4. Do not make claims about facts that are missing.
                5. Do not calculate a new numeric score.
                6. Do not compare against products that are not provided.
                7. Clearly mention when information is unavailable.
                8. Keep the explanation concise and easy to understand.
                9. The explanation must directly answer "Why this?"

                USER QUERY:
                """);

        prompt.append(safe(userQuery));
        prompt.append("\n\nPRODUCT DATA:\n");

        prompt.append("Name: ")
                .append(safe(product.getName()))
                .append("\n");

        prompt.append("Brand: ")
                .append(safe(product.getBrand()))
                .append("\n");

        prompt.append("Category: ")
                .append(safe(product.getCategory()))
                .append("\n");

        prompt.append("Price: ")
                .append(
                        product.getPrice() != null
                                ? product.getPrice().toPlainString()
                                : "Unavailable"
                )
                .append("\n");

        prompt.append("Rating: ")
                .append(
                        product.getRating() != null
                                ? product.getRating().toPlainString()
                                : "Unavailable"
                )
                .append("\n");

        prompt.append("Description: ")
                .append(safe(product.getDescription()))
                .append("\n");

        prompt.append("Availability: ")
                .append(safe(product.getAvailability()))
                .append("\n");

        prompt.append("Provider: ")
                .append(safe(product.getProvider()))
                .append("\n");

        prompt.append("Live Data: ")
                .append(product.isLive())
                .append("\n");

        prompt.append("Fetched At: ")
                .append(
                        product.getFetchedAt() != null
                                ? product.getFetchedAt().toString()
                                : "Unavailable"
                )
                .append("\n");

        prompt.append("External Product ID: ")
                .append(safe(product.getExternalProductId()))
                .append("\n");

        prompt.append("Attributes:\n");

        Map<String, String> attributes =
                product.getAttributes();

        if (attributes == null || attributes.isEmpty()) {

            prompt.append("Unavailable\n");

        } else {

            for (Map.Entry<String, String> entry
                    : attributes.entrySet()) {

                prompt.append("- ")
                        .append(safe(entry.getKey()))
                        .append(": ")
                        .append(safe(entry.getValue()))
                        .append("\n");
            }
        }

        prompt.append("""
                
                RESPONSE FORMAT:

                Give 2 to 4 short bullet points explaining
                why this product matches the user's request.

                End with one short sentence:
                "Information above is based only on the provided product data."
                """);

        return prompt.toString();
    }

    private static String safe(String value) {

        return value == null || value.isBlank()
                ? "Unavailable"
                : value;
    }
}