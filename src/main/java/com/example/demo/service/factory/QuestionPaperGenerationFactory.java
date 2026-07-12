package com.example.demo.service.factory;

import com.example.demo.dto.GenerateQuestionPaperRequest;
import com.example.demo.service.strategy.QuestionGenerationStrategy;
import com.example.demo.service.strategy.impl.HybridGenerationStrategy;
import com.example.demo.service.strategy.impl.NotesGenerationStrategy;
import com.example.demo.service.strategy.impl.PatternGenerationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionPaperGenerationFactory {

    private final HybridGenerationStrategy hybridStrategy;
    private final NotesGenerationStrategy notesStrategy;
    private final PatternGenerationStrategy patternStrategy;
    private boolean hasDocuments(List<?> list) {
        return list != null && !list.isEmpty();
    }

    public QuestionGenerationStrategy getStrategy(
            GenerateQuestionPaperRequest request) {



        boolean hasNotes =
                hasDocuments(request.getStudyMaterialIds());

        boolean hasPreviousPapers =
                hasDocuments(request.getPreviousPaperIds());

        if (hasNotes && hasPreviousPapers) {
            return hybridStrategy;
        }

        if (hasNotes) {
            return notesStrategy;
        }

        if (hasPreviousPapers) {
            return patternStrategy;
        }

        throw new IllegalArgumentException(
                "Please upload at least one study material or previous question paper."
        );
    }
}