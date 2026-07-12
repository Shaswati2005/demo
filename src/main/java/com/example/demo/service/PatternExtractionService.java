package com.example.demo.service;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.entity.Document;
import java.util.List;

public interface PatternExtractionService {

    ExamPatternDTO extractPattern(List<Document> previousPapers);

}