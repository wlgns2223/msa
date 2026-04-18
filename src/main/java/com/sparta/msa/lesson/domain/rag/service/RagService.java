package com.sparta.msa.lesson.domain.rag.service;

import com.sparta.msa.lesson.domain.rag.dto.response.AnswerResponse;
import com.sparta.msa.lesson.domain.rag.dto.response.RagResponse;
import com.sparta.msa.lesson.global.enums.DomainExceptionCode;
import com.sparta.msa.lesson.global.exception.DomainException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

  private final ChatClient chatClient;
  private final VectorStore vectorStore;

  private static final String RAG_PROMPT_TEMPLATE = """
      다음 문서들을 참고하여 질문에 답변해주세요.
      문서에 없는 내용은 답변하지 마세요.
      답변은 한국어로 작성해주세요.
      
      [참고 문서]
      %s
      
      [질문]
      %s
      
      [답변]
      """;

  public AnswerResponse ask(String question) {
    List<Document> relevantDocs = searchDocuments(question, 5, 0.0);
    if (relevantDocs.isEmpty()) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND);
    }

    return AnswerResponse.builder()
        .answer(generateAnswer(question, relevantDocs))
        .build();
  }

  public AnswerResponse askInDocument(String question, String documentId) {
    List<Document> documents = searchDocumentsWithFilter(question, documentId, 3);
    if (documents.isEmpty()) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND);
    }

    String answer = chatClient.prompt()
        .system("당신은 전문 문서 기반 응답 시스템입니다. 제공된 문서 내용만 사용하세요")
        .user(String.format(RAG_PROMPT_TEMPLATE, combineDocuments(documents), question))
        .call()
        .content();

    return AnswerResponse.builder().answer(answer).build();
  }

  public RagResponse askWithSource(String question) {
    List<Document> docs = searchDocuments(question, 5, 0.7);
    String answer = generateAnswer(question, docs);

    List<RagResponse.DocumentSource> sources = docs.stream()
        .map(doc -> RagResponse.DocumentSource.builder()
            .fileName((String) doc.getMetadata().getOrDefault("fileName", "unknown"))
            .documentId(doc.getId())
            .preview(doc.getText().substring(0, Math.min(doc.getText().length(), 100)))
            .build())
        .toList();
    return RagResponse.builder()
        .answer(answer)
        .sources(sources)
        .build();
  }

  private List<Document> searchDocumentsWithFilter(String question, String documentId, int topK) {
    return vectorStore.similaritySearch(
        SearchRequest.builder()
            .query(question)
            .topK(topK)
            .filterExpression("document_id == '" + documentId + "'")
            .build()
    );
  }

  public List<Document> searchDocuments(String query, int topK, double threshold) {
    return vectorStore.similaritySearch(SearchRequest.builder()
        .query(query)
        .topK(topK)
        .similarityThreshold(threshold)
        .build());
  }

  private String generateAnswer(String question, List<Document> docs) {
    return chatClient.prompt()
        .user(String.format(RAG_PROMPT_TEMPLATE, combineDocuments(docs), question))
        .call()
        .content();
  }

  private String combineDocuments(List<Document> documents) {
    return documents.stream()
        .map(doc -> String.format("[%s]: %s",
            doc.getMetadata().getOrDefault("fileName", "Unknown"),
            doc.getText())

        ).collect(Collectors.joining("\n\n--\n\n"));
  }


}
