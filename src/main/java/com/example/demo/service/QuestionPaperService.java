package com.example.demo.service;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;

public interface QuestionPaperService {

    QuestionPaperResponse generateQuestionPaper(
            GenerateQuestionPaperRequest request
    );

}