package com.example.demo.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilarChunkDTO {

    private UUID chunkId;

    private UUID documentId;

    private String filename;

    private Integer chunkIndex;

    private String chunkText;

    private Double score;

}