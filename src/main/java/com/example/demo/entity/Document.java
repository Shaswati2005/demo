package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String sourceType;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}