package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

public class ChunkingUtil {

    private static final int CHUNK_SIZE = 3000;
    private static final int OVERLAP = 200;

    public static List<String> splitIntoChunks(String text) {

        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        int start = 0;
        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            // Try to break at a sentence boundary (period, newline) within the last 300 chars
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf('.', end);
                int lastNewline = text.lastIndexOf('\n', end);
                int breakPoint = Math.max(lastPeriod, lastNewline);
                if (breakPoint > start + CHUNK_SIZE / 2) {
                    end = breakPoint + 1;
                }
            }

            chunks.add(text.substring(start, end).trim());

            // Advance with overlap for context continuity
            start = end - OVERLAP;
            if (start < 0) start = 0;
            // Prevent infinite loop if overlap pushes start back
            if (start <= end - CHUNK_SIZE && end < text.length()) {
                start = end;
            }
        }

        return chunks;
    }
}