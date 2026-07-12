package com.example.demo.service.impl;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.service.QuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionPaperServiceImpl
        implements QuestionPaperService {

    @Override
    public QuestionPaperResponse generateQuestionPaper(
            GenerateQuestionPaperRequest request
    ) {

        return null;

    }

}