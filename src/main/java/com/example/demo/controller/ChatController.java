package com.example.demo.controller;

import com.example.demo.dto.ChatRequestDTO;
import com.example.demo.dto.ChatResponseDTO;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponseDTO chat(
            @RequestBody ChatRequestDTO request
    ) {

        return chatService.askQuestion(
                request.getQuestion()
        );

    }

}