package com.sparta.msa.lesson.domain.rag.controller;

import com.sparta.msa.lesson.domain.rag.dto.request.QuestionRequest;
import com.sparta.msa.lesson.domain.rag.dto.response.RagResponse;
import com.sparta.msa.lesson.domain.rag.service.RagService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/rag")
@RequiredArgsConstructor
@RestController
public class RagController {

  private final RagService ragService;

  @PostMapping("/ask-with-source")
  public ApiResponse<RagResponse> askWithSource(@RequestBody QuestionRequest request) {
    return ApiResponse.ok(ragService.askWithSource(request.getQuestion()));
  }

}
