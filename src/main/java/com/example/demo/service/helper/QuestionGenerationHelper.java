package com.example.demo.service.helper;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.entity.Document;
import com.example.demo.entity.GeneratedQuestionPaper;
import com.example.demo.service.ChatService;
import com.example.demo.service.GeneratedQuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionGenerationHelper {

    private final ChatService chatService;

    private final GeneratedQuestionPaperService
            generatedQuestionPaperService;

    public QuestionPaperResponse generateAndSave(

            GenerateQuestionPaperRequest request,

            String prompt,

            List<Document> studyMaterials,

            List<Document> previousPapers

    ) {

        String generatedPaper =
                chatService.generate(prompt);

        GeneratedQuestionPaper paper =
                GeneratedQuestionPaper.builder()

                        .subject(request.getSubject())

                        .difficulty(request.getDifficulty())

                        .bloomLevel(request.getBloomLevel())

                        .totalMarks(request.getTotalMarks())

                        .generatedPaper(generatedPaper)

                        .prompt(prompt)

                        .studyMaterials(studyMaterials)

                        .previousPapers(previousPapers)

                        .build();

        generatedQuestionPaperService.save(paper);

        return QuestionPaperResponse.builder()

                .questionPaper(generatedPaper)

                .build();

    }

}