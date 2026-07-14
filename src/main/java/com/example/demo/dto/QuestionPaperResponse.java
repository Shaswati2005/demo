package com.example.demo.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPaperResponse {

    private UUID id;
    private String questionPaper;

}