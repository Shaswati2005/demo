package com.example.demo.dto.embedding;

import lombok.Data;

import java.util.List;

@Data
public class OllamaEmbeddingResponse {

    private String model;

    private List<List<Double>> embeddings;

}