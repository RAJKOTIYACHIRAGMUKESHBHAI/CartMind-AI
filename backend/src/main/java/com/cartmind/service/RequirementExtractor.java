package com.cartmind.service;

import com.cartmind.dto.ExtractedRequirements;
import com.cartmind.prompt.RequirementPrompt;
import com.cartmind.util.FallbackParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class RequirementExtractor {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;

    @Value("${bedrock.model-id}")
    private String modelId;

    public RequirementExtractor(
            BedrockRuntimeClient bedrockRuntimeClient,
            ObjectMapper objectMapper
    ) {
        this.bedrockRuntimeClient = bedrockRuntimeClient;
        this.objectMapper = objectMapper;
    }

    public ExtractedRequirements extract(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        try {
            return extractUsingBedrock(query);

        } catch (SdkException | JacksonException e) {

            System.out.println(
                    "Bedrock unavailable. Using fallback parser. Reason: "
                            + e.getMessage()
            );

            return FallbackParser.parse(query);
        }
    }

    private ExtractedRequirements extractUsingBedrock(String query) {

        String prompt = RequirementPrompt.build(query);

        Message message = Message.builder()
                .role("user")
                .content(ContentBlock.fromText(prompt))
                .build();

        ConverseRequest request = ConverseRequest.builder()
                .modelId(modelId)
                .messages(message)
                .inferenceConfig(config -> config
                        .maxTokens(1000)
                        .temperature(0.0f)
                )
                .build();

        ConverseResponse response =
                bedrockRuntimeClient.converse(request);

        String responseText = extractResponseText(response);

        String cleanJson = cleanJson(responseText);

        try {
            return objectMapper.readValue(
                    cleanJson,
                    ExtractedRequirements.class
            );
        } catch (JacksonException e) {
            throw e;
        }
    }

    private String extractResponseText(
            ConverseResponse response
    ) {

        if (response.output() == null
                || response.output().message() == null
                || response.output().message().content() == null
                || response.output().message().content().isEmpty()) {

            throw new IllegalStateException(
                    "Bedrock returned an empty response"
            );
        }

        return response.output()
                .message()
                .content()
                .get(0)
                .text();
    }

    private String cleanJson(String responseText) {

        if (responseText == null || responseText.isBlank()) {

            throw new IllegalStateException(
                    "Bedrock returned empty text"
            );
        }

        String cleaned = responseText.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();

        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(
                    0,
                    cleaned.length() - 3
            ).trim();
        }

        return cleaned;
    }
}