package com.example.demo.dto.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OllamaChatRequest {

    private String model;

    private String prompt;

    @Builder.Default
    private boolean stream = false;

}