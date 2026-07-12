package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Document;
import com.example.demo.mapper.DocumentMapper;
import com.example.demo.service.DocumentService;
import com.example.demo.util.PaginationUtil;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload/pdf")
    public DocumentResponseDTO uploadPdf(@RequestParam MultipartFile file, @RequestParam String subject) throws Exception {

        return DocumentMapper.toDto(documentService.uploadPdf(file, subject));
    }

    @PostMapping("/upload/docx")
    public DocumentResponseDTO uploadDocx(@RequestParam MultipartFile file, @RequestParam String subject) throws Exception {

        return DocumentMapper.toDto(documentService.uploadDocx(file, subject));
    }

    @PostMapping("/upload/text")
    public DocumentResponseDTO uploadText(@RequestBody TextUploadRequest request) {

        return DocumentMapper.toDto(documentService.uploadText(request.getTitle(), request.getSubject(), request.getText()));
    }

//    @GetMapping
//    public Page<DocumentResponseDTO> getAllDocuments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "uploadedAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
//
//        return documentService.getAllDocuments(page, size, sortBy, direction).map(DocumentMapper::toDto);
//    }

    @GetMapping
    public PagedResponse<DocumentResponseDTO> getAllDocuments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")
            int size, @RequestParam(defaultValue = "uploadedAt")
            String sortBy, @RequestParam(defaultValue = "desc")
            String direction) {

        Page<Document> pageResult = documentService.getAllDocuments(page, size, sortBy, direction);

        return PaginationUtil.buildResponse(pageResult, DocumentMapper::toDto);
    }

    @GetMapping("/{id}")
    public DocumentResponseDTO getDocumentById(@PathVariable UUID id) {
        return DocumentMapper.toDto(documentService.getDocumentById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        Document document = documentService.getDocumentById(id);

        Resource resource = (Resource) documentService.downloadDocument(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFilename() + "\"").body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public PagedResponse<DocumentResponseDTO> searchDocuments(@RequestParam String query, @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "uploadedAt") String sortBy,
                                                              @RequestParam(defaultValue = "desc") String direction) {
        Page<Document> pageResult = documentService.searchDocuments(query, page, size, sortBy, direction);
        return PaginationUtil.buildResponse(pageResult, DocumentMapper::toDto);
    }

    @GetMapping("/filter")
    public PagedResponse<DocumentResponseDTO> filterDocuments(@RequestParam(required = false) String subject, @RequestParam(required = false) String documentType,
                                                              @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "uploadedAt") String sortBy,
                                                              @RequestParam(defaultValue = "desc") String direction) {

        Page<Document> pageResult = documentService.filterDocuments(subject, documentType, status, page, size, sortBy, direction);

        return PaginationUtil.buildResponse(pageResult, DocumentMapper::toDto);
    }

    @GetMapping("/stats")
    public DocumentStatsDTO getStats() {
        return documentService.getDocumentStats();
    }

    @GetMapping("/advanced-search")
    public PagedResponse<DocumentResponseDTO> advancedSearch(@RequestParam(required = false) String query, @RequestParam(required = false) String subject,
                                                             @RequestParam(required = false) String documentType, @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "uploadedAt") String sortBy, @RequestParam(defaultValue = "desc") String direction) {

        Page<Document> pageResult = documentService.searchAndFilterDocuments(query, subject, documentType, status, page, size, sortBy, direction);
        return PaginationUtil.buildResponse(pageResult, DocumentMapper::toDto);
    }

    @GetMapping("/{id}/details")
    public DocumentDetailResponseDTO getDocumentDetails(@PathVariable UUID id) {
        Document document = documentService.getDocumentById(id);
        return DocumentMapper.toDetailDto(document);
    }

    @GetMapping("/{id}/chunks")
    public List<DocumentChunkDTO> getChunks(@PathVariable UUID id) {
        return documentService.getDocumentChunks(id);
    }

    @PostMapping("/semantic-search")
    public List<SimilarChunkDTO> semanticSearch(@RequestBody SearchRequestDTO request) {
        return documentService.semanticSearch(request.getQuestion(), request.getTopK());
    }
}