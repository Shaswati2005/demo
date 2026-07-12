package com.example.demo.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamPatternDTO {

    private String title;

    private Integer totalMarks;

    private String duration;

    @Builder.Default
    private List<String> instructions = new ArrayList<>();

    @Builder.Default
    private List<ExamSectionDTO> sections = new ArrayList<>();

}