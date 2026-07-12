package com.example.demo.service;

import com.example.demo.entity.GeneratedQuestionPaper;

import java.util.List;
import java.util.UUID;

public interface GeneratedQuestionPaperService {

    GeneratedQuestionPaper save(GeneratedQuestionPaper paper);

    List<GeneratedQuestionPaper> getAll();

    GeneratedQuestionPaper getById(UUID id);

    void delete(UUID id);

}