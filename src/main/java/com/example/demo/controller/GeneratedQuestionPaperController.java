package com.example.demo.controller;

import com.example.demo.dto.GeneratedQuestionPaperResponseDTO;
import com.example.demo.entity.GeneratedQuestionPaper;
import com.example.demo.mapper.GeneratedQuestionPaperMapper;
import com.example.demo.service.GeneratedQuestionPaperService;
import com.example.demo.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable UUID id) throws Exception {
        GeneratedQuestionPaper paper = service.getById(id);

        byte[] pdfBytes = PdfGeneratorUtil.generateQuestionPaperPdf(
                paper.getSubject(),
                paper.getDifficulty(),
                paper.getBloomLevel(),
                paper.getTotalMarks(),
                paper.getGeneratedPaper()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "question_paper_" + id + ".pdf";
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename)
                        .build()
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}