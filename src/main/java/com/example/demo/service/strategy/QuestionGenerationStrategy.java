package com.example.demo.service.strategy;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;

public interface QuestionGenerationStrategy {

    QuestionPaperResponse generate(
            GenerateQuestionPaperRequest request
    );

}