package com.sparta.msa.lesson.domain.vector.service;

import com.sparta.msa.lesson.domain.vector.dto.response.DocumentUploadResponse;
import com.sparta.msa.lesson.domain.vector.dto.response.SimilaritySearchResponse;
import com.sparta.msa.lesson.domain.vector.dto.response.SimilaritySearchResponse.SearchResult;
import com.sparta.msa.lesson.domain.vector.entity.VectorDocument;
import com.sparta.msa.lesson.domain.vector.repository.VectorDocumentRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.Filter.ExpressionType;
import org.springframework.ai.vectorstore.filter.Filter.Key;
import org.springframework.ai.vectorstore.filter.Filter.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDocumentService {

  private final VectorStore vectorStore;
  private final VectorDocumentRepository vectorDocumentRepository;

  @Transactional
  public DocumentUploadResponse uploadDocument(MultipartFile file) throws IOException {
    String fileName = file.getName();
    String contentType = file.getContentType();
    String content = new String(file.getBytes(), StandardCharsets.UTF_8);

    VectorDocument vectorDocument = VectorDocument.builder()
        .id(UUID.randomUUID())
        .fileName(fileName)
        .content(content)
        .contentType(contentType)
        .build();

    VectorDocument savedDocument = vectorDocumentRepository.save(vectorDocument);
    List<Document> chunks = createChunks(content, savedDocument);

    vectorDocument.assignChunkCounter(chunks.size());
    vectorStore.add(chunks);

    return DocumentUploadResponse.builder()
        .documentId(savedDocument.getId().toString())
        .fileName(savedDocument.getFileName())
        .chunkCount(savedDocument.getChunkCount())
        .build();

  }

  private List<Document> createChunks(String content, VectorDocument entity) {
    TextSplitter splitter = new TokenTextSplitter(500, 100, 5, 10_000, true);

    Map<String, Object> metadata = Map.of(
        "document_id", entity.getId().toString(),
        "fileName", entity.getFileName(),
        "source", "user_upload"
    );

    Document rawVectorDocument = new Document(content, metadata);
    List<Document> splitChunks = splitter.split(rawVectorDocument);

    return splitChunks.stream()
        .filter(Objects::nonNull)
        .map(c -> new Document(UUID.randomUUID().toString(), c.getText(), c.getMetadata()))
        .toList();
  }

  public SimilaritySearchResponse searchSimilarity(UUID documentId, String query, Integer topK) {

    List<Document> searchResults = vectorStore.similaritySearch(
        SearchRequest.builder()
            .filterExpression(new Filter.Expression(
                ExpressionType.EQ,
                new Key("document_id"),
                new Value(documentId.toString())

            ))
            .query(query)
            .topK(topK)
            .build()
    );

    List<SearchResult> results = searchResults.stream()
        .map(doc -> SimilaritySearchResponse.SearchResult.builder()
            .id(doc.getId())
            .content(doc.getText())
            .metadata(doc.getMetadata())
            .build()
        ).toList();

    return SimilaritySearchResponse.builder()
        .query(query)
        .resultCount(results.size())
        .results(results)
        .build();

  }

  @Transactional
  public void deleteDocument(UUID documentId) {
    VectorDocument entity = vectorDocumentRepository.findById(documentId).orElseThrow();

    vectorDocumentRepository.delete(entity);
    try {
      List<String> chunkIds = vectorStore.similaritySearch(
          SearchRequest.builder()
              .query("*")
              .filterExpression(new Filter.Expression(
                  ExpressionType.EQ,
                  new Key("document_id"),
                  new Value(documentId.toString())
              ))
              .topK(10_000)
              .build()
      ).stream().map(Document::getId).toList();

      if (!chunkIds.isEmpty()) {
        vectorStore.delete(chunkIds);

      }

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

}
