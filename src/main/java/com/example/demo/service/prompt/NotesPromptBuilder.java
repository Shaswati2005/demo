package com.example.demo.service.prompt;

import org.springframework.stereotype.Component;

@Component
public class NotesPromptBuilder {

    public String build(

            String subject,

            String difficulty,

            String bloomLevel,

            Integer totalMarks,

            String context

    ) {

        return """
                You are an experienced university professor.

                Generate a university examination paper.

                Subject:
                %s

                Difficulty:
                %s

                Bloom Level:
                %s

                Total Marks:
                %d

                Study Material:

                %s

                Rules:

                - Use ONLY the supplied study material.
                - Cover different concepts.
                - Do not provide answers.

                Return only the question paper.
                """
                .formatted(
                        subject,
                        difficulty,
                        bloomLevel,
                        totalMarks,
                        context
                );

    }

}