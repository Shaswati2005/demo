package com.example.demo.service.prompt;

import org.springframework.stereotype.Component;

@Component
public class PatternPromptBuilder {

    public String build(

            String subject,

            String difficulty,

            String bloomLevel,

            Integer totalMarks,

            String pattern

    ) {

        return """
                You are an experienced university professor.

                Generate a new examination paper.

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

                Rules:

                - Follow the professor pattern.
                - Create completely new questions.
                - Do not repeat previous questions.
                - Do not provide answers.

                Return only the question paper.
                """
                .formatted(
                        subject,
                        difficulty,
                        bloomLevel,
                        totalMarks,
                        pattern
                );

    }

}