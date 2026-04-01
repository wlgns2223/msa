package com.sparta.msa.lesson.domain.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContextChatResponse {

  String message;

  String conversationId;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime timestamp;

  TokenUsage tokenUsage;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class TokenUsage {

    Integer promptTokens;

    Integer completionTokens;

    Integer totalTokens;
  }

}
