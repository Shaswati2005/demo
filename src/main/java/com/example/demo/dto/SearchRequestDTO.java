package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDTO {

    private String question;

    @Builder.Default
    private int topK = 5;

}