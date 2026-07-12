package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedQuestionPaperResponseDTO {

    private UUID id;

    private String subject;

    private String difficulty;

    private String bloomLevel;

    private Integer totalMarks;

    private String generatedPaper;

    private LocalDateTime generatedAt;

}