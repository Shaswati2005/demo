package com.example.demo.service;

import com.example.demo.dto.DocumentChunkDTO;
import com.example.demo.dto.DocumentStatsDTO;
import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.entity.Document;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    List<SimilarChunkDTO> semanticSearch(String question, int topK);

    List<SimilarChunkDTO> semanticSearch(String question, List<UUID> documentIds, int topK);

    Document uploadPdf(MultipartFile file, String subject) throws Exception;

    Document uploadDocx(MultipartFile file, String subject) throws Exception;

    Document uploadText(String title, String subject, String text);

    List<Document> getAllDocuments();

    Document getDocumentById(UUID id);

    FileSystemResource downloadDocument(UUID id);

    void deleteDocument(UUID id);

    Page<Document> searchDocuments(String query, int page, int size, String sortBy, String direction);

    Page<Document> getAllDocuments(int page, int size, String sortBy, String direction);

    Page<Document> filterDocuments(String subject, String documentType, String status, int page, int size, String sortBy, String direction);

    DocumentStatsDTO getDocumentStats();

    Page<Document> searchAndFilterDocuments(String query, String subject, String documentType, String status, int page, int size, String sortBy, String direction);

    List<DocumentChunkDTO> getDocumentChunks(UUID documentId);

    List<Document> getDocumentsByIds(List<UUID> ids);
}
