package com.example.demo.service.impl;

import com.example.demo.dto.embedding.OllamaEmbeddingRequest;
import com.example.demo.dto.embedding.OllamaEmbeddingResponse;
import com.example.demo.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final RestClient restClient;
    @Value("${ollama.embedding-model}")
    private String embeddingModel;

    @Override
    public List<Double> generateEmbedding(String text) {

        String input = text.length() > 4000
                ? text.substring(0, 4000)
                : text;

        OllamaEmbeddingRequest request = OllamaEmbeddingRequest.builder()
                .model(embeddingModel)
                .input(input)
                .build();

        try {

            OllamaEmbeddingResponse response = restClient.post()
                    .uri("/api/embed")
                    .body(request)
                    .retrieve()
                    .body(OllamaEmbeddingResponse.class);

            if (response == null ||
                    response.getEmbeddings() == null ||
                    response.getEmbeddings().isEmpty()) {

                throw new RuntimeException("Ollama returned an empty embedding.");

            }

            return response.getEmbeddings().getFirst();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate embedding from Ollama. Make sure Ollama is running and the model is installed.",
                    e
            );

        }
    }

}