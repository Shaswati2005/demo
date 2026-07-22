package com.example.demo.service.impl;

import com.example.demo.dto.gemini.GeminiEmbeddingRequest;
import com.example.demo.dto.gemini.GeminiEmbeddingResponse;
import com.example.demo.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final RestClient restClient;
    private final String embeddingModel;
    private final String apiKey;

    public EmbeddingServiceImpl(
            RestClient restClient,
            @Value("${gemini.embedding-model}") String embeddingModel,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.restClient = restClient;
        this.embeddingModel = embeddingModel;
        this.apiKey = apiKey;
    }

    @Override
    public List<Double> generateEmbedding(String text) {

        String input = text.length() > 4000
                ? text.substring(0, 4000)
                : text;

        GeminiEmbeddingRequest request = GeminiEmbeddingRequest.of(embeddingModel, input);

        String uri = "/v1beta/models/" + embeddingModel + ":embedContent?key=" + apiKey;

        try {

            GeminiEmbeddingResponse response = restClient.post()
                    .uri(uri)
                    .body(request)
                    .retrieve()
                    .body(GeminiEmbeddingResponse.class);

            if (response == null ||
                    response.getEmbedding() == null ||
                    response.getEmbedding().getValues() == null ||
                    response.getEmbedding().getValues().isEmpty()) {

                throw new RuntimeException("Gemini returned an empty embedding.");

            }

            return response.getEmbedding().getValues();

        } catch (org.springframework.web.client.RestClientResponseException e) {

            throw new RuntimeException(
                    "Failed to generate embedding from Gemini API. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString(),
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate embedding from Gemini API: " + e.getMessage(),
                    e
            );

        }
    }

}