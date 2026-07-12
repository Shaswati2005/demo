package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class RagIngestionService {

    public void processDocument(UUID documentId, MultipartFile file) {

        log.info("Dummy RAG processing started for document {}", documentId);

        /*
         TODO

         extract pdf

         chunk

         generate embeddings

         store in qdrant
         */

        log.info("Dummy chunk created");

        log.info("Dummy embeddings generated");

        log.info("Dummy vectors stored");
    }

    public void deleteDocumentVectors(
            UUID documentId
    ) {

        /*
         TODO

         delete vectors from qdrant
         */

        log.info("Dummy vector deletion for {}", documentId);
    }
}
