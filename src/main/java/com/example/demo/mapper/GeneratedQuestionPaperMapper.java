package com.example.demo.mapper;

import com.example.demo.dto.GeneratedQuestionPaperResponseDTO;
import com.example.demo.entity.GeneratedQuestionPaper;

public class GeneratedQuestionPaperMapper {

    private GeneratedQuestionPaperMapper(){}

    public static GeneratedQuestionPaperResponseDTO toDto(
            GeneratedQuestionPaper paper){

        return GeneratedQuestionPaperResponseDTO.builder()

                .id(paper.getId())

                .subject(paper.getSubject())

                .difficulty(paper.getDifficulty())

                .bloomLevel(paper.getBloomLevel())

                .totalMarks(paper.getTotalMarks())

                .generatedPaper(paper.getGeneratedPaper())

                .generatedAt(paper.getGeneratedAt())

                .build();

    }

}