package com.example.demo.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadDocumentResponseDto {

    private UUID documentId;
    private String message;
}
