package com.example.demo.service;

import com.example.demo.dto.ExamPatternDTO;

public interface PatternExtractionService {

    ExamPatternDTO extractPattern(String text);

}