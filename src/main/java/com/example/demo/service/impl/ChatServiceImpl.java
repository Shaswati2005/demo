package com.example.demo.service.impl;

import com.example.demo.dto.ChatResponseDTO;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.dto.chat.OllamaChatRequest;
import com.example.demo.dto.chat.OllamaChatResponse;
import com.example.demo.service.ChatService;
import com.example.demo.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final DocumentService documentService;

    private final RestClient restClient;

    @org.springframework.beans.factory.annotation.Value("${ollama.chat-model:qwen3:4b}")
    private String chatModel;

    @Override
    public ChatResponseDTO askQuestion(String question) {

        List<SimilarChunkDTO> chunks =
                documentService.semanticSearch(question, 5);

        String prompt = buildPrompt(question, chunks);

        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .model(chatModel)
                        .prompt(prompt)
                        .stream(false)
                        .build();

        OllamaChatResponse response =
                restClient.post()
                        .uri("/api/generate")
                        .body(request)
                        .retrieve()
                        .body(OllamaChatResponse.class);

        return ChatResponseDTO.builder()
                .answer(response.getResponse())
                .sources(chunks)
                .build();

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

        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .model(chatModel)
                        .prompt(prompt)
                        .stream(false)
                        .build();

        OllamaChatResponse response =
                restClient.post()
                        .uri("/api/generate")
                        .body(request)
                        .retrieve()
                        .body(OllamaChatResponse.class);

        if (response == null || response.getResponse() == null) {
            throw new RuntimeException("Failed to generate response from Ollama.");
        }

        return response.getResponse();
    }
}