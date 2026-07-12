package com.example.demo.service.impl;

import com.example.demo.dto.ExamPatternDTO;
import com.example.demo.dto.chat.OllamaChatRequest;
import com.example.demo.dto.chat.OllamaChatResponse;
import com.example.demo.service.PatternExtractionService;
import com.example.demo.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class PatternExtractionServiceImpl
        implements PatternExtractionService {

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    @Override
    public ExamPatternDTO extractPattern(String text) {

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

        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .model("qwen3:4b")
                        .prompt(prompt)
                        .stream(false)
                        .build();

        OllamaChatResponse response =
                restClient.post()
                        .uri("/api/generate")
                        .body(request)
                        .retrieve()
                        .body(OllamaChatResponse.class);

        try {

            String json =
                    JsonUtil.extractJson(
                            response.getResponse()
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