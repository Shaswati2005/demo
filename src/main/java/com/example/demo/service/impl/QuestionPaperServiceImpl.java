package com.example.demo.service.impl;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.entity.Document;
import com.example.demo.entity.GeneratedQuestionPaper;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionPaperServiceImpl implements QuestionPaperService {


    private final DocumentService documentService;
    private final ChatService chatService;
    private final PatternExtractionService patternExtractionService;
    private final GeneratedQuestionPaperService generatedQuestionPaperService;

    @Override
    public QuestionPaperResponse generateQuestionPaper(
            GenerateQuestionPaperRequest request) {

        if (request.getStudyMaterialIds() == null ||
                request.getStudyMaterialIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one study material."
            );
        }

        if (request.getPreviousPaperIds() == null ||
                request.getPreviousPaperIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one previous year paper."
            );
        }

        String searchQuery =
                request.getSubject() + " " + request.getDifficulty();

        List<SimilarChunkDTO> chunks =
                documentService.semanticSearch(
                        searchQuery,
                        request.getStudyMaterialIds(),
                        5
                );

        List<Document> previousPapers =
                documentService.getDocumentsByIds(
                        request.getPreviousPaperIds()
                );

        StringBuilder previousPaperText =
                new StringBuilder();

        for (Document document : previousPapers) {

            previousPaperText
                    .append(document.getExtractedText())
                    .append("\n\n========================\n\n");

        }

        ExamPatternDTO pattern =
                patternExtractionService.extractPattern(
                        previousPaperText.toString()
                );

        StringBuilder context =
                new StringBuilder();

        for (SimilarChunkDTO chunk : chunks) {

            context.append("---------------------------------\n");

            context.append("Document: ")
                    .append(chunk.getFilename())
                    .append("\n\n");

            context.append(chunk.getChunkText())
                    .append("\n\n");

        }

        String prompt =
                buildPrompt(
                        request,
                        context.toString(),
                        pattern
                );

        String paper =
                chatService.generate(prompt);

        GeneratedQuestionPaper generatedPaper =
                GeneratedQuestionPaper.builder()

                        .subject(request.getSubject())

                        .difficulty(request.getDifficulty())

                        .bloomLevel(request.getBloomLevel())

                        .totalMarks(request.getTotalMarks())

                        .generatedPaper(paper)

                        .prompt(prompt)

                        .studyMaterials(
                                documentService.getDocumentsByIds(
                                        request.getStudyMaterialIds()
                                )
                        )

                        .previousPapers(
                                documentService.getDocumentsByIds(
                                        request.getPreviousPaperIds()
                                )
                        )

                        .build();

        generatedQuestionPaperService.save(generatedPaper);

        return QuestionPaperResponse.builder()
                .questionPaper(paper)
                .build();

    }

    private String buildPrompt(
            GenerateQuestionPaperRequest request,
            String context,
            ExamPatternDTO pattern
    ) {

        return """
                You are an experienced university professor.

                Generate a professional university examination paper.

                Subject:
                %s

                Difficulty:
                %s

                Bloom's Taxonomy Level:
                %s

                Total Marks:
                %d

                Pattern:

                %d questions of 2 marks

                %d questions of 5 marks

                %d questions of 10 marks

                Professor Pattern:

                %s

                Instructions:

                • Use ONLY the supplied study material.
                • Follow the professor pattern as closely as possible.
                • Do not use outside knowledge.
                • Do not generate duplicate questions.
                • Cover different concepts.
                • Mention marks beside every question.
                • Do not provide answers.

                Generate every question according to the requested Bloom's Taxonomy level.

                Bloom Levels:

                Remember
                - Recall facts
                - Define
                - List
                - Name

                Understand
                - Explain
                - Describe
                - Summarize

                Apply
                - Solve
                - Demonstrate
                - Compute
                - Use concepts

                Analyze
                - Compare
                - Differentiate
                - Analyze
                - Examine

                Evaluate
                - Justify
                - Critique
                - Defend

                Create
                - Design
                - Develop
                - Propose
                - Construct

                Use the requested Bloom level consistently across the entire question paper.

                Study Material:

                %s

                Return ONLY the generated question paper.
                """.formatted(

                request.getSubject(),
                request.getDifficulty(),
                request.getBloomLevel(),
                request.getTotalMarks(),
                request.getTwoMarkQuestions(),
                request.getFiveMarkQuestions(),
                request.getTenMarkQuestions(),
                pattern.toString(),
                context

        );

    }

}