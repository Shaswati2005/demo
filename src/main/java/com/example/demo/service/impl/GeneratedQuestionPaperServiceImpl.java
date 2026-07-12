package com.example.demo.service.impl;

import com.example.demo.entity.GeneratedQuestionPaper;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.GeneratedQuestionPaperRepository;
import com.example.demo.service.GeneratedQuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneratedQuestionPaperServiceImpl
        implements GeneratedQuestionPaperService {

    private final GeneratedQuestionPaperRepository repository;

    @Override
    public GeneratedQuestionPaper save(
            GeneratedQuestionPaper paper) {

        return repository.save(paper);

    }

    @Override
    public List<GeneratedQuestionPaper> getAll() {

        return repository.findAll();

    }

    @Override
    public GeneratedQuestionPaper getById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Generated question paper not found."
                        ));

    }

    @Override
    public void delete(UUID id) {

        GeneratedQuestionPaper paper = getById(id);

        repository.delete(paper);

    }

}