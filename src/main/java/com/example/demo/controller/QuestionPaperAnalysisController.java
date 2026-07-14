package com.example.demo.controller;

import com.example.demo.dto.QuestionPaperAnalysisResponseDTO;
import com.example.demo.service.QuestionPaperAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class QuestionPaperAnalysisController {

    private final QuestionPaperAnalysisService service;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionPaperAnalysisResponseDTO analyzeUploadedFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subject") String subject
    ) throws Exception {
        return service.analyzeUploadedFile(file, subject);
    }

    @PostMapping("/document/{documentId}")
    public QuestionPaperAnalysisResponseDTO analyzeExistingDocument(
            @PathVariable UUID documentId
    ) {
        return service.analyzeExistingDocument(documentId);
    }

    @GetMapping
    public List<QuestionPaperAnalysisResponseDTO> getAll() {
        return service.getAllAnalyses();
    }

    @GetMapping("/{id}")
    public QuestionPaperAnalysisResponseDTO getById(@PathVariable UUID id) {
        return service.getAnalysisById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteAnalysis(id);
    }
}
