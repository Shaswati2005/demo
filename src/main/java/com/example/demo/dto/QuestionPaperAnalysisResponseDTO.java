package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPaperAnalysisResponseDTO {
    private UUID id;
    private UUID documentId;
    private String filename;
    private String subject;
    private LocalDateTime analyzedAt;
    private QuestionPaperAnalysisResultDTO analysisResult;
}
