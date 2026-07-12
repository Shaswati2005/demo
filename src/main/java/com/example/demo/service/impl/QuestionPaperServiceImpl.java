package com.example.demo.service.impl;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.service.QuestionPaperService;
import com.example.demo.service.factory.QuestionPaperGenerationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionPaperServiceImpl
        implements QuestionPaperService {

    private final QuestionPaperGenerationFactory factory;

    @Override
    public QuestionPaperResponse generateQuestionPaper(
            GenerateQuestionPaperRequest request) {

        if ((request.getStudyMaterialIds() == null || request.getStudyMaterialIds().isEmpty())
                && (request.getPreviousPaperIds() == null || request.getPreviousPaperIds().isEmpty())) {

            throw new IllegalArgumentException(
                    "Please select at least one study material or previous question paper."
            );
        }

        return factory
                .getStrategy(request)
                .generate(request);
    }

}