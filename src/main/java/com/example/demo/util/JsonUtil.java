package com.example.demo.util;

public class JsonUtil {

    private JsonUtil() {}

    public static String extractJson(String text) {

        if (text == null || text.isBlank()) {
            throw new RuntimeException("Empty LLM response.");
        }

        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        if (start == -1 || end == -1 || start >= end) {
            throw new RuntimeException("No JSON found in response.");
        }

        return text.substring(start, end + 1);
    }
}