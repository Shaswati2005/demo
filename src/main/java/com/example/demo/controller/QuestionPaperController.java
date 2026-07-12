package com.example.demo.controller;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.dto.QuestionPaperResponse;
import com.example.demo.service.QuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-paper")
@RequiredArgsConstructor
public class QuestionPaperController {

    private final QuestionPaperService questionPaperService;

    @PostMapping("/generate")
    public QuestionPaperResponse generate(
            @RequestBody GenerateQuestionPaperRequest request
    ) {

        return questionPaperService.generateQuestionPaper(request);

    }

}