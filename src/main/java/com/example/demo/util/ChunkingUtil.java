package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

public class ChunkingUtil {

    private static final int CHUNK_SIZE = 1000;

    public static List<String> splitIntoChunks(String text) {

        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        int start = 0;
        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }
}