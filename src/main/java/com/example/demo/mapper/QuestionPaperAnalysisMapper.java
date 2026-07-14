package com.example.demo.mapper;

import com.example.demo.dto.QuestionPaperAnalysisResponseDTO;
import com.example.demo.dto.QuestionPaperAnalysisResultDTO;
import com.example.demo.entity.QuestionPaperAnalysis;

public class QuestionPaperAnalysisMapper {

    private QuestionPaperAnalysisMapper() {}

    public static QuestionPaperAnalysisResponseDTO toDto(
            QuestionPaperAnalysis analysis,
            QuestionPaperAnalysisResultDTO resultDto
    ) {
        return QuestionPaperAnalysisResponseDTO.builder()
                .id(analysis.getId())
                .documentId(analysis.getDocument() != null ? analysis.getDocument().getId() : null)
                .filename(analysis.getFilename())
                .subject(analysis.getSubject())
                .analyzedAt(analysis.getAnalyzedAt())
                .analysisResult(resultDto)
                .build();
    }
}
