package com.example.demo.repository;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {

    List<DocumentChunk> findByDocument(Document document);
    void deleteByDocument(Document document);
    List<DocumentChunk> findByDocumentOrderByChunkIndexAsc(Document document    );

    List<DocumentChunk> findByDocumentAndChunkIndexBetweenOrderByChunkIndexAsc(
            Document document,
            Integer start,
            Integer end
    );
}