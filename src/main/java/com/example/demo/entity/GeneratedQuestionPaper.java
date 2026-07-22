package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "generated_question_papers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedQuestionPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String subject;

    private String difficulty;

    private String bloomLevel;

    private Integer totalMarks;

    @Column(columnDefinition = "TEXT")
    private String generatedPaper;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @CreationTimestamp
    private LocalDateTime generatedAt;

    @ManyToMany
    @JoinTable(
            name = "generated_paper_study_documents",
            joinColumns = @JoinColumn(name = "generated_paper_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> studyMaterials;

    @ManyToMany
    @JoinTable(
            name = "generated_paper_previous_documents",
            joinColumns = @JoinColumn(name = "generated_paper_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> previousPapers;

}