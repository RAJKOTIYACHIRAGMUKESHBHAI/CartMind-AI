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

        prompt.append("You are CartMind AI.\n\n")
                .append("Explain why the selected product matches the user's\n")
                .append("shopping requirements.\n\n")
                .append("STRICT TRUST RULES:\n")
                .append("1. Use ONLY facts provided in PRODUCT DATA.\n")
                .append("2. Do not invent specifications.\n")
                .append("3. Do not invent price, rating, availability,\n")
                .append("   discounts, reviews, or performance claims.\n")
                .append("4. Do not make claims about facts that are missing.\n")
                .append("5. Do not calculate a new numeric score.\n")
                .append("6. Do not compare against products that are not provided.\n")
                .append("7. Clearly mention when information is unavailable.\n")
                .append("8. Keep the explanation concise and easy to understand.\n")
                .append("9. The explanation must directly answer \"Why this?\"\n\n")
                .append("USER QUERY:\n");

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

        prompt.append("\nRESPONSE FORMAT:\n\n")
                .append("Give 2 to 4 short bullet points explaining\n")
                .append("why this product matches the user's request.\n\n")
                .append("End with one short sentence:\n")
                .append("\"Information above is based only on the provided product data.\"\n");

        return prompt.toString();
    }

    private static String safe(String value) {

        return value == null || value.trim().isEmpty()
                ? "Unavailable"
                : value;
    }
}