package com.example.demo.service.strategy.impl;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.entity.Document;
import com.example.demo.service.DocumentService;
import com.example.demo.service.PatternExtractionService;
import com.example.demo.service.helper.QuestionGenerationHelper;
import com.example.demo.service.prompt.HybridPromptBuilder;
import com.example.demo.service.strategy.QuestionGenerationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HybridGenerationStrategy implements QuestionGenerationStrategy {

    private final DocumentService documentService;

    private final PatternExtractionService patternExtractionService;

    private final QuestionGenerationHelper helper;

    private final HybridPromptBuilder promptBuilder;

    private final ObjectMapper objectMapper;

    @Override
    public QuestionPaperResponse generate(
            GenerateQuestionPaperRequest request) {

        // STEP 1 : Semantic Search

        String searchQuery =
                request.getSubject() + " " + request.getDifficulty();

        List<SimilarChunkDTO> chunks =
                documentService.semanticSearch(
                        searchQuery,
                        request.getStudyMaterialIds(),
                        5
                );

        // STEP 2 : Build Context

        String context = buildContext(chunks);

        // STEP 3 : Load Previous Papers

        List<Document> previousPapers =
                documentService.getDocumentsByIds(
                        request.getPreviousPaperIds()
                );

        // STEP 4 : Extract Professor Pattern

        ExamPatternDTO pattern =
                patternExtractionService.extractPattern(
                        previousPapers
                );

        // STEP 5 : Convert Pattern DTO -> JSON

        String patternJson;

        try {

            patternJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(pattern);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to serialize exam pattern.",
                    e
            );

        }

        // STEP 6 : Build Prompt

        String prompt =
                promptBuilder.build(
                        request.getSubject(),
                        request.getDifficulty(),
                        request.getBloomLevel(),
                        request.getTotalMarks(),
                        patternJson,
                        context
                );

        // STEP 7 : Load Study Materials

        List<Document> studyMaterials =
                documentService.getDocumentsByIds(
                        request.getStudyMaterialIds()
                );

        // STEP 8 : Generate & Save

        return helper.generateAndSave(
                request,
                prompt,
                studyMaterials,
                previousPapers
        );

    }

    private String buildContext(List<SimilarChunkDTO> chunks) {

        StringBuilder builder = new StringBuilder();

        for (SimilarChunkDTO chunk : chunks) {

            builder.append("---------------------------------\n");

            builder.append("Document: ")
                    .append(chunk.getFilename())
                    .append("\n\n");

            builder.append(chunk.getChunkText())
                    .append("\n\n");

        }

        return builder.toString();

    }

}