package com.example.demo.dto.gemini;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiEmbeddingRequest {

    private String model;

    private Content content;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    public static GeminiEmbeddingRequest of(String modelName, String text) {
        String fullModel = modelName.startsWith("models/") ? modelName : "models/" + modelName;
        return GeminiEmbeddingRequest.builder()
                .model(fullModel)
                .content(Content.builder()
                        .parts(List.of(Part.builder().text(text).build()))
                        .build())
                .build();
    }
}
