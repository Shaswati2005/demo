package com.example.demo.service;

import com.example.demo.dto.ChatResponseDTO;

public interface ChatService {

    ChatResponseDTO askQuestion(String question);
    String generate(String prompt);

}