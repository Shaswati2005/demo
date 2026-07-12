package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSectionDTO {

    private String sectionName;

    private Integer marksPerQuestion;

    private Integer questionCount;

    private String attemptRule;

}