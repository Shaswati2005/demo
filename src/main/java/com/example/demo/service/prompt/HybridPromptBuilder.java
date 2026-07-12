package com.example.demo.service.prompt;

import org.springframework.stereotype.Component;

@Component
public class HybridPromptBuilder {

    public String build(

            String subject,

            String difficulty,

            String bloomLevel,

            Integer totalMarks,

            String pattern,

            String context

    ) {

        return """
                You are an experienced university professor.

                Generate a professional examination paper.

                Subject:
                %s

                Difficulty:
                %s

                Bloom Level:
                %s

                Total Marks:
                %d

                Professor Pattern:

                %s

                Study Material:

                %s

                Rules:

                - Follow the professor pattern.
                - Use ONLY the supplied study material.
                - Do not create duplicate questions.
                - Do not provide answers.

                Return only the question paper.
                """
                .formatted(
                        subject,
                        difficulty,
                        bloomLevel,
                        totalMarks,
                        pattern,
                        context
                );

    }

}