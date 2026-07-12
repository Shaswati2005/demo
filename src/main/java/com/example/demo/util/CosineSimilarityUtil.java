package com.example.demo.util;

import java.util.List;

public class CosineSimilarityUtil {

    public static double cosineSimilarity(
            List<Double> vectorA,
            List<Double> vectorB
    ) {

        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Embedding dimensions do not match.");
        }

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < vectorA.size(); i++) {

            dotProduct += vectorA.get(i) * vectorB.get(i);

            normA += vectorA.get(i) * vectorA.get(i);

            normB += vectorB.get(i) * vectorB.get(i);
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}