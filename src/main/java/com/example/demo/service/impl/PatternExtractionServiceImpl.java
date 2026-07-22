package com.example.demo.service.impl;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.dto.gemini.GeminiRequest;
import com.example.demo.dto.gemini.GeminiResponse;
import com.example.demo.entity.Document;
import com.example.demo.service.PatternExtractionService;
import com.example.demo.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class PatternExtractionServiceImpl
        implements PatternExtractionService {

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    private final String chatModel;

    private final String apiKey;

    public PatternExtractionServiceImpl(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${gemini.chat-model}") String chatModel,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
        this.apiKey = apiKey;
    }

    @Override
    public ExamPatternDTO extractPattern(List<Document> previousPapers) {

        // Combine all previous papers into one text
        StringBuilder builder = new StringBuilder();

        for (Document document : previousPapers) {

            builder.append("---------------------------------\n");
            builder.append("Document: ")
                    .append(document.getFilename())
                    .append("\n\n");

            builder.append(document.getExtractedText())
                    .append("\n\n");
        }

        String text = builder.toString();

        String prompt = """
                You are an expert university examination analyst.
                
                Analyze the following previous year examination paper.
                
                Extract ONLY the examination structure.
                
                Do NOT include any explanations.
                
                Return ONLY a valid JSON object.
                
                Do NOT wrap the JSON in markdown.
                
                Do NOT write ```json.
                
                The JSON MUST exactly match:
                
                {
                  "title":"",
                  "duration":"",
                  "totalMarks":0,
                  "instructions":[],
                  "sections":[
                    {
                      "sectionName":"",
                      "marksPerQuestion":0,
                      "questionCount":0,
                      "attemptRule":""
                    }
                  ]
                }
                
                Question Paper:
                
                %s
                """.formatted(text);

        GeminiRequest request = GeminiRequest.of(prompt);

        java.net.URI uri = java.net.URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + chatModel + ":generateContent?key=" + apiKey);

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

        try {

            String json =
                    JsonUtil.extractJson(
                            response.getText()
                    );

            return objectMapper.readValue(
                    json,
                    ExamPatternDTO.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse exam pattern.",
                    e
            );

        }

    }

}