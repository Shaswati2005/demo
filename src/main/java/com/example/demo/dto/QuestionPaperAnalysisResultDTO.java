package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPaperAnalysisResultDTO {
    private List<ImportantTopicDTO> importantTopics;
    private List<String> recommendedStudyOrder;
}
