package com.sparta.msa.lesson.domain.ai.controller;

import com.sparta.msa.lesson.domain.ai.dto.request.ContextChatRequest;
import com.sparta.msa.lesson.domain.ai.dto.response.ContextChatResponse;
import com.sparta.msa.lesson.domain.ai.dto.response.ImageAnalysisResponse;
import com.sparta.msa.lesson.domain.ai.service.AiChatService;
import com.sparta.msa.lesson.domain.ai.service.PersistedChatService;
import com.sparta.msa.lesson.domain.ai.service.VisionChatService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiChatController {

  private final AiChatService aiChatService;
  private final PersistedChatService persistedChatService;
  private final VisionChatService visionChatService;

  @PostMapping("/simple")
  public ApiResponse<ContextChatResponse> simpleChat(@RequestBody ContextChatRequest request) {
    return ApiResponse.ok(aiChatService.chat(request.getMessage()));
  }

//    @PostMapping
//    public ApiResponse<ContextChatResponse> chat(@RequestBody ContextChatRequest request) {
//        return ApiResponse.ok(aiChatService.chatWithHistory(request.getMessage(), request.getConversationId()));
//    }

  @PostMapping
  public ApiResponse<ContextChatResponse> chat(@RequestBody ContextChatRequest request) {
    return ApiResponse.ok(
        persistedChatService.chat(request.getConversationId(), request.getMessage()));
  }

  @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> streamChat(@RequestBody ContextChatRequest request) {
    return aiChatService.chatStream(request.getMessage());
  }

  @PostMapping(value = "/vision", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ImageAnalysisResponse> analysisImage(
      @RequestParam String message,
      @RequestParam MultipartFile image
  ) {
    return ApiResponse.ok(visionChatService.analysisImage(message, image));
  }

}
