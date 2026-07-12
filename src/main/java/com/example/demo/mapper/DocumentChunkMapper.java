package com.example.demo.mapper;

import com.example.demo.dto.DocumentChunkDTO;
import com.example.demo.entity.DocumentChunk;

public class DocumentChunkMapper {

    public static DocumentChunkDTO toDto(
            DocumentChunk chunk
    ) {

        return DocumentChunkDTO.builder()
                .chunkIndex(chunk.getChunkIndex())
                .chunkText(chunk.getChunkText())
                .build();
    }
}