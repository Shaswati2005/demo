package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentDetailResponseDTO {
    private UUID id;
    private String filename;
    private String subject;
    private String documentType;
    private String sourceType;
    private String status;
    private String filePath;
    private String previewText;
    private LocalDateTime uploadedAt;
}