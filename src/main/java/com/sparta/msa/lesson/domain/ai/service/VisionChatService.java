package com.sparta.msa.lesson.domain.ai.service;

import com.sparta.msa.lesson.domain.ai.dto.response.ImageAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisionChatService {

  private final ChatClient chatClient;

  @Transactional
  public ImageAnalysisResponse analysisImage(String message, MultipartFile image) {
    String contentType = image.getContentType();
    if (contentType == null) {
      contentType = "image/jpeg";
    }

    try {

      String finalContentType = contentType;
      var response = chatClient.prompt()
          .user(u -> u.text(message)
              .media(MimeTypeUtils.parseMimeType(finalContentType), image.getResource()))
          .call()
          .chatResponse();

      String analysis = response.getResult().getOutput().getText();

      ImageAnalysisResponse.TokenUsage tokenUsage = null;
      Usage usage = response.getMetadata().getUsage();
      if (usage != null) {
        tokenUsage = ImageAnalysisResponse.TokenUsage.builder()
            .promptTokens(usage.getPromptTokens())
            .completionTokens(usage.getCompletionTokens())
            .totalTokens(usage.getTotalTokens())
            .build();
      }

      return ImageAnalysisResponse.builder()
          .analysis(analysis)
          .imageType(finalContentType)
          .imageSize(image.getSize())
          .usage(tokenUsage)
          .build();


    } catch (Exception e) {
      log.error("이미지 분석 처리 중 오류 발생: {}", e.getMessage());
      throw new RuntimeException(e);
    }

  }


}
