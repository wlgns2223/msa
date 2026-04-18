package com.sparta.msa.lesson.domain.function.controller;

import com.sparta.msa.lesson.domain.function.dto.request.QuestionRequest;
import com.sparta.msa.lesson.domain.function.dto.response.AnswerResponse;
import com.sparta.msa.lesson.domain.function.service.FunctionService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/function")
@RequiredArgsConstructor
public class FunctionCallingController {

  private final FunctionService functionService;

  @PostMapping("/chat")
  public ApiResponse<AnswerResponse> chat(@RequestBody QuestionRequest request) {
    String result = functionService.chat(request.getQuestion());
    return ApiResponse.ok(AnswerResponse.builder().answer(result).build());
  }

}
