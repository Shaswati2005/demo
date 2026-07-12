package com.example.demo.dto;

import com.example.demo.entity.Document;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO  {

    private UUID id;
    private String filename;
    private String subject;
    private String documentType;
    private String sourceType;
    private String status;
    private LocalDateTime uploadedAt;
}