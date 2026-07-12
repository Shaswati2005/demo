package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateQuestionPaperRequest {

    private String subject;

    private String syllabus;

    private String difficulty;

    private Integer totalMarks;

    private Integer twoMarkQuestions;

    private Integer fiveMarkQuestions;

    private Integer tenMarkQuestions;

}