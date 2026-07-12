package com.example.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class EmbeddingUtil {

    private static final ObjectMapper mapper =
            new ObjectMapper();

    public static List<Double> fromJson(
            String json
    ) {

        try {

            return mapper.readValue(
                    json,
                    new TypeReference<List<Double>>() {}
            );

        } catch (Exception e) {

            throw new RuntimeException("Unable to parse embedding.", e);

        }

    }

}