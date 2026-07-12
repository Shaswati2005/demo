package com.example.demo.util;

import com.example.demo.dto.SimilarChunkDTO;

import java.util.*;

public class ContextMergeUtil {

    public static List<SimilarChunkDTO> merge(List<SimilarChunkDTO> chunks) {

        if (chunks.isEmpty()) {
            return chunks;
        }

        chunks.sort(
                Comparator.comparing(SimilarChunkDTO::getDocumentId)
                        .thenComparing(SimilarChunkDTO::getChunkIndex)
        );

        List<SimilarChunkDTO> merged = new ArrayList<>();

        SimilarChunkDTO current = chunks.get(0);

        for (int i = 1; i < chunks.size(); i++) {

            SimilarChunkDTO next = chunks.get(i);

            boolean sameDocument =
                    current.getDocumentId().equals(next.getDocumentId());

            boolean adjacent =
                    next.getChunkIndex() <= current.getChunkIndex() + 1;

            if (sameDocument && adjacent) {

                current.setChunkText(
                        current.getChunkText()
                                + "\n\n"
                                + next.getChunkText()
                );

                current.setScore(
                        Math.max(
                                current.getScore(),
                                next.getScore()
                        )
                );

            } else {

                merged.add(current);
                current = next;

            }

        }

        merged.add(current);

        return merged;

    }

}