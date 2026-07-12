package com.example.demo.specification;

import com.example.demo.entity.Document;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> hasSubject(String subject) {
        return (root, query, cb) ->
                subject == null || subject.isBlank() ? null : cb.equal(root.get("subject"), subject);
    }

    public static Specification<Document> hasDocumentType(String documentType) {
        return (root, query, cb) ->
                documentType == null || documentType.isBlank() ? null : cb.equal(root.get("documentType"), documentType);
    }

    public static Specification<Document> hasStatus(String status) {
        return (root, query, cb) ->
                status == null || status.isBlank() ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Document> containsText(String query) {
        return (root, q, cb) ->
                query == null || query.isBlank() ? null : cb.like(cb.lower(root.get("extractedText")),
                        "%" + query.toLowerCase() + "%"
                );
    }
}