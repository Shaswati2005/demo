package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SemanticSearchResponseDTO {

    private String filename;
    private String chunkText;
    private Double score;
}