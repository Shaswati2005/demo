package com.example.demo.service.strategy.impl;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.entity.Document;
import com.example.demo.service.DocumentService;
import com.example.demo.service.helper.QuestionGenerationHelper;
import com.example.demo.service.prompt.NotesPromptBuilder;
import com.example.demo.service.strategy.QuestionGenerationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesGenerationStrategy
        implements QuestionGenerationStrategy {

    private final DocumentService documentService;

    private final QuestionGenerationHelper helper;

    private final NotesPromptBuilder promptBuilder;

    @Override
    public QuestionPaperResponse generate(
            GenerateQuestionPaperRequest request) {

        String searchQuery =
                request.getSubject() + " " + request.getDifficulty();

        List<SimilarChunkDTO> chunks =
                documentService.semanticSearch(
                        searchQuery,
                        request.getStudyMaterialIds(),
                        5
                );

        String context = buildContext(chunks);

        String prompt =
                promptBuilder.build(
                        request.getSubject(),
                        request.getDifficulty(),
                        request.getBloomLevel(),
                        request.getTotalMarks(),
                        context
                );

        List<Document> studyMaterials =
                documentService.getDocumentsByIds(
                        request.getStudyMaterialIds()
                );

        return helper.generateAndSave(
                request,
                prompt,
                studyMaterials,
                List.of()
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