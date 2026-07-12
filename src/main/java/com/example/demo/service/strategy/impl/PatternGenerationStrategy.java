package com.example.demo.service.strategy.impl;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.entity.Document;
import com.example.demo.service.DocumentService;
import com.example.demo.service.PatternExtractionService;
import com.example.demo.service.helper.QuestionGenerationHelper;
import com.example.demo.service.prompt.PatternPromptBuilder;
import com.example.demo.service.strategy.QuestionGenerationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatternGenerationStrategy
        implements QuestionGenerationStrategy {

    private final DocumentService documentService;

    private final PatternExtractionService patternExtractionService;

    private final PatternPromptBuilder promptBuilder;

    private final QuestionGenerationHelper helper;

    private final ObjectMapper objectMapper;

    @Override
    public QuestionPaperResponse generate(
            GenerateQuestionPaperRequest request) {

        List<Document> previousPapers =
                documentService.getDocumentsByIds(
                        request.getPreviousPaperIds()
                );

        ExamPatternDTO pattern =
                patternExtractionService.extractPattern(
                        previousPapers
                );

        String patternJson;

        try {

            patternJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(pattern);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

        String prompt =
                promptBuilder.build(
                        request.getSubject(),
                        request.getDifficulty(),
                        request.getBloomLevel(),
                        request.getTotalMarks(),
                        patternJson
                );

        return helper.generateAndSave(
                request,
                prompt,
                List.of(),
                previousPapers
        );

    }

}