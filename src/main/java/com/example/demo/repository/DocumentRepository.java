package com.example.demo.repository;

import com.example.demo.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID>, JpaSpecificationExecutor<Document> {
    @Query(
            value = """
                SELECT *
                FROM documents
                WHERE extracted_text ILIKE CONCAT('%', :query, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM documents
                WHERE extracted_text ILIKE CONCAT('%', :query, '%')
                """,
            nativeQuery = true
    )
    Page<Document> searchDocuments(@Param("query") String query, Pageable pageable);
    long countByDocumentType(String documentType);
    long countByUploadedAtAfter(LocalDateTime dateTime);
}

