package com.example.demo.controller;

import com.example.demo.dto.GeneratedQuestionPaperResponseDTO;
import com.example.demo.mapper.GeneratedQuestionPaperMapper;
import com.example.demo.service.GeneratedQuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/generated-papers")
@RequiredArgsConstructor
public class GeneratedQuestionPaperController {

    private final GeneratedQuestionPaperService service;

    @GetMapping
    public List<GeneratedQuestionPaperResponseDTO> getAll() {

        return service.getAll()
                .stream()
                .map(GeneratedQuestionPaperMapper::toDto)
                .toList();

    }

    @GetMapping("/{id}")
    public GeneratedQuestionPaperResponseDTO getById(
            @PathVariable UUID id){

        return GeneratedQuestionPaperMapper.toDto(
                service.getById(id)
        );

    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id){

        service.delete(id);

    }

}