package com.example.demo.service.impl;

import com.example.demo.dto.DocumentChunkDTO;
import com.example.demo.dto.DocumentStatsDTO;
import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentChunk;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.DocumentChunkMapper;
import com.example.demo.repository.DocumentChunkRepository;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.DocumentService;
import com.example.demo.service.EmbeddingService;
import com.example.demo.specification.DocumentSpecification;
import com.example.demo.util.ChunkingUtil;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import com.example.demo.dto.SimilarChunkDTO;
import com.example.demo.util.CosineSimilarityUtil;
import com.example.demo.util.EmbeddingUtil;

import java.util.Comparator;
import java.util.ArrayList;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.demo.util.ContextMergeUtil;


@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository repository;

    private final DocumentChunkRepository chunkRepository;

    private final EmbeddingService embeddingService;

    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${rag.similarity.threshold}")
    private double similarityThreshold;

    @Value("${rag.top-k}")
    private int defaultTopK;


    @Override
    public Document uploadPdf(MultipartFile file, String subject) throws Exception {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir, filename);

        Files.createDirectories(path.getParent());

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        String extractedText = extractPdfText(path);

        Document document = Document.builder()
                .filename(file.getOriginalFilename())
                .subject(subject)
                .documentType("PDF")
                .sourceType("PDF")
                .status("UPLOADED")
                .filePath(path.toString())
                .extractedText(extractedText)
                .build();

        Document savedDocument = repository.save(document);
        createChunks(savedDocument);
        return savedDocument;
    }

    @Override
    public Document uploadDocx(MultipartFile file, String subject) throws Exception {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir, filename);

        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        String extractedText = extractDocxText(path);

        Document document = Document.builder()
                .filename(file.getOriginalFilename())
                .subject(subject)
                .documentType("DOCX")
                .sourceType("DOCX")
                .status("UPLOADED")
                .filePath(path.toString())
                .extractedText(extractedText)
                .build();

        Document savedDocument = repository.save(document);
        createChunks(savedDocument);
        return savedDocument;
    }

    @Override
    public Document uploadText(String title, String subject, String text) {

        Document document = Document.builder()
                .filename(title)
                .subject(subject)
                .documentType("TEXT")
                .sourceType("TEXT")
                .status("UPLOADED")
                .filePath("N/A")
                .extractedText(text)
                .build();
        Document savedDocument = repository.save(document);
        createChunks(savedDocument);
        return savedDocument;
    }

    @Override
    public List<Document> getAllDocuments() {
        return repository.findAll();
    }

    @Override
    public Document getDocumentById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }

    @Override
    public FileSystemResource downloadDocument(UUID id) {

        Document document = getDocumentById(id);
        Path path = Paths.get(document.getFilePath());
        return new FileSystemResource(path);
    }


    @Override
    public void deleteDocument(UUID id) {
        Document document = getDocumentById(id);
        try {
            if (!"N/A".equals(document.getFilePath())) {
                Files.deleteIfExists(Paths.get(document.getFilePath()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete file");
        }
        chunkRepository.deleteByDocument(document);
        repository.delete(document);
    }

    private String extractPdfText(Path path) throws Exception {

        try (PDDocument pdf = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdf);
        }
    }

    private String extractDocxText(Path path) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(path))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }

    @Override
    public Page<Document> searchDocuments(String query, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.searchDocuments(query, pageable);
    }

    @Override
    public Page<Document> getAllDocuments(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }

    @Override
    public Page<Document> filterDocuments(String subject, String documentType, String status, int page, int size, String sortBy, String direction) {

        Specification<Document> specification = Specification.where(DocumentSpecification.hasSubject(subject))
                        .and(DocumentSpecification.hasDocumentType(documentType))
                        .and(DocumentSpecification.hasStatus(status));

        Sort sort = direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(specification, pageable);
    }


    @Override
    public DocumentStatsDTO getDocumentStats() {

        LocalDateTime today = LocalDateTime.now().minusDays(1);
        LocalDateTime week = LocalDateTime.now().minusDays(7);

        return DocumentStatsDTO.builder().totalDocuments(repository.count())
                .pdfCount(repository.countByDocumentType("PDF"))
                .docxCount(repository.countByDocumentType("DOCX"))
                .textCount(repository.countByDocumentType("TEXT"))
                .uploadedToday(repository.countByUploadedAtAfter(today))
                .uploadedThisWeek(repository.countByUploadedAtAfter(week))
                .build();
    }

    @Override
    public Page<Document> searchAndFilterDocuments(String query, String subject, String documentType, String status, int page, int size, String sortBy, String direction) {

        Specification<Document> specification = Specification.where(DocumentSpecification.containsText(query))
                        .and(DocumentSpecification.hasSubject(subject))
                        .and(DocumentSpecification.hasDocumentType(documentType))
                        .and(DocumentSpecification.hasStatus(status));

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAll(specification, pageable);
    }



    private void createChunks(Document document) {

        List<String> chunks = ChunkingUtil.splitIntoChunks(document.getExtractedText());

        for (int i = 0; i < chunks.size(); i++) {

            String chunkText = chunks.get(i);

            try {

                List<Double> embedding =
                        embeddingService.generateEmbedding(chunkText);

                String embeddingJson =
                        objectMapper.writeValueAsString(embedding);

                DocumentChunk chunk = DocumentChunk.builder()
                        .document(document)
                        .chunkIndex(i)
                        .chunkText(chunkText)
                        .embedding(embeddingJson)
                        .build();

                chunkRepository.save(chunk);

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to generate embedding for chunk " + i + ": " + e.getMessage(),
                        e
                );

            }
        }
    }


    @Override
    public List<DocumentChunkDTO> getDocumentChunks(UUID documentId) {

        Document document = getDocumentById(documentId);
        return chunkRepository.findByDocumentOrderByChunkIndexAsc(document).stream().map(DocumentChunkMapper::toDto).toList();
    }


    @Override
    public List<SimilarChunkDTO> semanticSearch(String question, int topK) {

        // Generate embedding for the user's question
        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        List<DocumentChunk> chunks =
                chunkRepository.findAll();

        List<SimilarChunkDTO> results =
                new ArrayList<>();

        for (DocumentChunk chunk : chunks) {

            if (chunk.getEmbedding() == null ||
                    chunk.getEmbedding().isBlank()) {
                continue;
            }

            List<Double> chunkEmbedding =
                    EmbeddingUtil.fromJson(chunk.getEmbedding());

            double similarity =
                    CosineSimilarityUtil.cosineSimilarity(
                            questionEmbedding,
                            chunkEmbedding
                    );

            results.add(

                    SimilarChunkDTO.builder()

                            .chunkId(chunk.getId())

                            .documentId(chunk.getDocument().getId())

                            .filename(chunk.getDocument().getFilename())

                            .chunkIndex(chunk.getChunkIndex())

                            .chunkText(expandChunk(chunk))

                            .score(similarity)

                            .build()

            );

        }

        List<SimilarChunkDTO> filtered = results.stream()

                .filter(result ->
                        result.getScore() >= similarityThreshold
                )

                .sorted(
                        Comparator.comparing(
                                SimilarChunkDTO::getScore
                        ).reversed()
                )

                .limit(topK > 0 ? topK : defaultTopK)

                .toList();

        return ContextMergeUtil.merge(filtered);


    }


    private String expandChunk(DocumentChunk chunk) {

        int start = Math.max(0, chunk.getChunkIndex() - 2);

        int end = chunk.getChunkIndex() + 2;

        List<DocumentChunk> neighbours =
                chunkRepository.findByDocumentAndChunkIndexBetweenOrderByChunkIndexAsc(
                        chunk.getDocument(),
                        start,
                        end
                );

        StringBuilder builder = new StringBuilder();

        for (DocumentChunk neighbour : neighbours) {

            builder.append(neighbour.getChunkText())
                    .append("\n\n");

        }

        return builder.toString();

    }


    @Override
    public List<SimilarChunkDTO> semanticSearch(
            String question,
            List<UUID> documentIds,
            int topK
    ) {

        // Generate embedding for the user's question
        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        // Only load chunks from the selected documents
        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentIdIn(documentIds);

        List<SimilarChunkDTO> results =
                new ArrayList<>();

        for (DocumentChunk chunk : chunks) {

            if (chunk.getEmbedding() == null ||
                    chunk.getEmbedding().isBlank()) {
                continue;
            }

            List<Double> chunkEmbedding =
                    EmbeddingUtil.fromJson(chunk.getEmbedding());

            double similarity =
                    CosineSimilarityUtil.cosineSimilarity(
                            questionEmbedding,
                            chunkEmbedding
                    );

            results.add(

                    SimilarChunkDTO.builder()

                            .chunkId(chunk.getId())

                            .documentId(chunk.getDocument().getId())

                            .filename(chunk.getDocument().getFilename())

                            .chunkIndex(chunk.getChunkIndex())

                            .chunkText(expandChunk(chunk))

                            .score(similarity)

                            .build()

            );

        }

        List<SimilarChunkDTO> filtered = results.stream()

                .filter(result ->
                        result.getScore() >= similarityThreshold
                )

                .sorted(
                        Comparator.comparing(
                                SimilarChunkDTO::getScore
                        ).reversed()
                )

                .limit(topK > 0 ? topK : defaultTopK)

                .toList();

        return ContextMergeUtil.merge(filtered);

    }


    @Override
    public List<Document> getDocumentsByIds(List<UUID> ids) {

        return repository.findAllById(ids);

    }

}