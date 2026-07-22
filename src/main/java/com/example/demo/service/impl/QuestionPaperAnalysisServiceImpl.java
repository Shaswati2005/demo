package com.example.demo.service.impl;

import com.example.demo.dto.QuestionPaperAnalysisResponseDTO;
import com.example.demo.dto.QuestionPaperAnalysisResultDTO;
import com.example.demo.dto.chat.OllamaChatRequest;
import com.example.demo.dto.chat.OllamaChatResponse;
import com.example.demo.entity.Document;
import com.example.demo.entity.QuestionPaperAnalysis;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.QuestionPaperAnalysisMapper;
import com.example.demo.repository.QuestionPaperAnalysisRepository;
import com.example.demo.service.DocumentService;
import com.example.demo.service.QuestionPaperAnalysisService;
import com.example.demo.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionPaperAnalysisServiceImpl implements QuestionPaperAnalysisService {

    private final DocumentService documentService;
    private final QuestionPaperAnalysisRepository repository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final String chatModel;

    public QuestionPaperAnalysisServiceImpl(
            DocumentService documentService,
            QuestionPaperAnalysisRepository repository,
            RestClient restClient,
            ObjectMapper objectMapper,
            @org.springframework.beans.factory.annotation.Value("${ollama.chat-model:qwen3:4b}") String chatModel
    ) {
        this.documentService = documentService;
        this.repository = repository;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
    }

    @Override
    public QuestionPaperAnalysisResponseDTO analyzeUploadedFile(MultipartFile file, String subject) throws Exception {
        Document document = uploadDocument(file, subject);
        return performAnalysis(document);
    }

    @Override
    public QuestionPaperAnalysisResponseDTO analyzeExistingDocument(UUID documentId) {
        Document document = documentService.getDocumentById(documentId);
        return performAnalysis(document);
    }

    @Override
    public List<QuestionPaperAnalysisResponseDTO> getAllAnalyses() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public QuestionPaperAnalysisResponseDTO getAnalysisById(UUID id) {
        QuestionPaperAnalysis analysis = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with id: " + id));
        return toResponseDto(analysis);
    }

    @Override
    public void deleteAnalysis(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Analysis not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private Document uploadDocument(MultipartFile file, String subject) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".docx")) {
            return documentService.uploadDocx(file, subject);
        } else {
            return documentService.uploadPdf(file, subject);
        }
    }

    private QuestionPaperAnalysisResponseDTO performAnalysis(Document document) {
        String prompt = """
                You are an expert university examination analyst.
                
                Analyze the following examination paper to predict the pattern of the future paper.
                
                Extract the key topics covered, their probability (0-100) of appearing in the next exam, the expected marks/weightage (e.g. [5], [5, 10]), the cognitive Bloom's level (e.g. Remember, Understand, Apply, Analyze, Evaluate, Create), and a reasoned explanation of why it will appear.
                Also suggest the optimal study order list.
                
                Do NOT include any explanations outside the JSON structure.
                
                Return ONLY a valid JSON object.
                
                Do NOT wrap the JSON in markdown.
                
                Do NOT write ```json.
                
                The JSON MUST exactly match this format:
                
                {
                  "importantTopics": [
                    {
                      "topic": "Deadlock",
                      "probability": 94,
                      "expectedMarks": [10],
                      "bloomLevel": "Analyze",
                      "reason": "Appears in 4 of the last 5 papers."
                    }
                  ],
                  "recommendedStudyOrder": [
                    "Deadlock",
                    "Paging"
                  ]
                }
                
                Question Paper Text:
                
                %s
                """.formatted(document.getExtractedText());

        OllamaChatRequest request = OllamaChatRequest.builder()
                .model(chatModel)
                .prompt(prompt)
                .stream(false)
                .build();

        OllamaChatResponse response = restClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(OllamaChatResponse.class);

        if (response == null || response.getResponse() == null) {
            throw new RuntimeException("Empty response from Ollama.");
        }

        try {
            String jsonStr = JsonUtil.extractJson(response.getResponse());
            QuestionPaperAnalysisResultDTO resultDto = objectMapper.readValue(jsonStr, QuestionPaperAnalysisResultDTO.class);

            QuestionPaperAnalysis analysis = QuestionPaperAnalysis.builder()
                    .document(document)
                    .filename(document.getFilename())
                    .subject(document.getSubject())
                    .analysisResult(jsonStr)
                    .build();

            QuestionPaperAnalysis saved = repository.save(analysis);
            return QuestionPaperAnalysisMapper.toDto(saved, resultDto);

        } catch (Exception e) {
            throw new RuntimeException("Unable to analyze question paper pattern. Raw response: " + response.getResponse(), e);
        }
    }

    private QuestionPaperAnalysisResponseDTO toResponseDto(QuestionPaperAnalysis analysis) {
        try {
            QuestionPaperAnalysisResultDTO resultDto = objectMapper.readValue(
                    analysis.getAnalysisResult(),
                    QuestionPaperAnalysisResultDTO.class
            );
            return QuestionPaperAnalysisMapper.toDto(analysis, resultDto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map saved analysis result.", e);
        }
    }
}
