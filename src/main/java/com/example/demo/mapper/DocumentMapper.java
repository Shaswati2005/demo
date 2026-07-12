package com.example.demo.mapper;

import com.example.demo.dto.DocumentDetailResponseDTO;
import com.example.demo.dto.DocumentResponseDTO;
import com.example.demo.entity.Document;

public class DocumentMapper {

    public static DocumentResponseDTO toDto(
            Document document
    ) {

        return DocumentResponseDTO.builder()
                .id(document.getId())
                .filename(document.getFilename())
                .subject(document.getSubject())
                .documentType(document.getDocumentType())
                .sourceType(document.getSourceType())
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    public static DocumentDetailResponseDTO toDetailDto(Document document) {
        String extractedText = document.getExtractedText();

        String previewText = extractedText == null ? "" : extractedText.substring(0, Math.min(1000, extractedText.length()));
        return DocumentDetailResponseDTO.builder()
                .id(document.getId())
                .filename(document.getFilename())
                .subject(document.getSubject())
                .documentType(document.getDocumentType())
                .sourceType(document.getSourceType())
                .status(document.getStatus())
                .filePath(document.getFilePath())
                .previewText(previewText)
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}