package com.example.demo.service.impl;

import com.example.demo.dto.ChatResponseDTO;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.dto.gemini.GeminiRequest;
import com.example.demo.dto.gemini.GeminiResponse;
import com.example.demo.service.ChatService;
import com.example.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final DocumentService documentService;

    private final RestClient restClient;

    private final String chatModel;

    private final String apiKey;

    public ChatServiceImpl(
            DocumentService documentService,
            RestClient restClient,
            @Value("${gemini.chat-model}") String chatModel,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.documentService = documentService;
        this.restClient = restClient;
        this.chatModel = chatModel;
        this.apiKey = apiKey;
    }

    @Override
    public ChatResponseDTO askQuestion(String question) {

        List<SimilarChunkDTO> chunks =
                documentService.semanticSearch(question, 5);

        String prompt = buildPrompt(question, chunks);

        GeminiRequest request = GeminiRequest.of(prompt);

        java.net.URI uri = java.net.URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + chatModel + ":generateContent?key=" + apiKey);

        try {
            GeminiResponse response =
                    restClient.post()
                            .uri(uri)
                            .body(request)
                            .retrieve()
                            .body(GeminiResponse.class);

            return ChatResponseDTO.builder()
                    .answer(response != null ? response.getText() : "")
                    .sources(chunks)
                    .build();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw new RuntimeException("Gemini API call failed. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString(), e);
        }

    }

    private String buildPrompt(
            String question,
            List<SimilarChunkDTO> chunks
    ) {

        StringBuilder builder = new StringBuilder();

        builder.append("""
                You are an AI assistant.

                Answer ONLY using the context below.

                If the answer is not found, say:

                "I couldn't find this information in the uploaded documents."

                Context:

                """);


        for (SimilarChunkDTO chunk : chunks) {

            builder.append("---------------------------------\n");
            builder.append("Document: ")
                    .append(chunk.getFilename())
                    .append("\n");

            builder.append("Similarity: ")
                    .append(String.format("%.3f", chunk.getScore()))
                    .append("\n\n");

            builder.append(chunk.getChunkText());

            builder.append("\n\n");

        }

        builder.append("Question:\n");
        builder.append(question);

        return builder.toString();

    }

    @Override
    public String generate(String prompt) {

        GeminiRequest request = GeminiRequest.of(prompt);

        java.net.URI uri = java.net.URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + chatModel + ":generateContent?key=" + apiKey);

        try {
            GeminiResponse response =
                    restClient.post()
                            .uri(uri)
                            .body(request)
                            .retrieve()
                            .body(GeminiResponse.class);

            if (response == null || response.getText() == null || response.getText().isEmpty()) {
                throw new RuntimeException("Failed to generate response from Gemini.");
            }

            return response.getText();
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw new RuntimeException("Gemini API call failed. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString(), e);
        }
    }
}