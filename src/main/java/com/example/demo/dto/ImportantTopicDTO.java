package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportantTopicDTO {
    private String topic;
    private Integer probability;
    private List<Integer> expectedMarks;
    private String bloomLevel;
    private String reason;
}
