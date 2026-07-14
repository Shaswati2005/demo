package com.example.demo.service;

import com.example.demo.dto.QuestionPaperAnalysisResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface QuestionPaperAnalysisService {
    QuestionPaperAnalysisResponseDTO analyzeUploadedFile(MultipartFile file, String subject) throws Exception;
    QuestionPaperAnalysisResponseDTO analyzeExistingDocument(UUID documentId);
    List<QuestionPaperAnalysisResponseDTO> getAllAnalyses();
    QuestionPaperAnalysisResponseDTO getAnalysisById(UUID id);
    void deleteAnalysis(UUID id);
}
