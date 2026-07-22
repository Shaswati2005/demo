package com.example.demo.service.impl;

import com.example.demo.dto.QuestionPaperAnalysisResponseDTO;
import com.example.demo.dto.QuestionPaperAnalysisResultDTO;
import com.example.demo.dto.gemini.GeminiRequest;
import com.example.demo.dto.gemini.GeminiResponse;
import com.example.demo.entity.Document;
import com.example.demo.entity.QuestionPaperAnalysis;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.QuestionPaperAnalysisMapper;
import com.example.demo.repository.QuestionPaperAnalysisRepository;
import com.example.demo.service.DocumentService;
import com.example.demo.service.QuestionPaperAnalysisService;
import com.example.demo.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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
    private final String apiKey;

    public QuestionPaperAnalysisServiceImpl(
            DocumentService documentService,
            QuestionPaperAnalysisRepository repository,
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${gemini.chat-model}") String chatModel,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.documentService = documentService;
        this.repository = repository;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
        this.apiKey = apiKey;
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

        GeminiRequest request = GeminiRequest.of(prompt);

        String uri = "/v1beta/models/" + chatModel + ":generateContent?key=" + apiKey;

        GeminiResponse response;
        try {
            response = restClient.post()
                    .uri(uri)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw new RuntimeException("Gemini API call failed. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString(), e);
        }

        if (response == null || response.getText() == null || response.getText().isEmpty()) {
            throw new RuntimeException("Empty response from Gemini.");
        }

        try {
            String jsonStr = JsonUtil.extractJson(response.getText());
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
            throw new RuntimeException("Unable to analyze question paper pattern. Raw response: " + response.getText(), e);
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
