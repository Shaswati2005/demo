package com.example.demo.dto.embedding;

import lombok.Data;

import java.util.List;

@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaEmbeddingResponse {

    private String model;

    private List<List<Double>> embeddings;

}