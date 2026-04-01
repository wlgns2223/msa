package com.sparta.msa.lesson.domain.ai.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/example")
public class AiChatExampleController {

  private final ChatClient.Builder clientBuilder;

  @PostMapping("/chat")
  public ApiResponse<String> chat(@RequestBody String message) {
    String res = clientBuilder.build()
        .prompt()
        .user(message) // prompt
        .call() // LLM API 전송
        .content(); // API 리턴값 문자열 반환

    return ApiResponse.ok(res);
  }

  @GetMapping("/marketing")
  public String generateMarketing(
      @RequestParam(value = "productName") String productName,
      @RequestParam(value = "features") String features
  ) {

    String template = """
        제품명 {productName}의 마케팅 문구를 작성하세요.
        주요 특징: {features}
        조건: 감성적이고 100자 이내로 작성할 것.
        """;

    return clientBuilder.build()
        .prompt()
        .user((u) -> u.text(template)
            .param("productName", productName)
            .param("features", features)

        )
        .call()
        .content();
  }

  @GetMapping("/translate")
  public String translate(
      @RequestParam(value = "text") String text,
      @RequestParam(value = "targetLanguage", defaultValue = "영어") String target
  ) {
    return clientBuilder.build()
        .prompt()
        .system("당신은 번역전문가 입니다. 주어진 텍스트를 문맥에 맞게 번역해 주세요")
        .user((u) -> u.text("다음 텍스트를 {lang}로 번역해주세요: {text}")
            .param("lang", target)
            .param("text", text)
        )
        .call()
        .content();
  }

  @GetMapping("/analyze")
  public ProductAnalysisResponse analysisReview(@RequestParam(value = "review") String review) {
    String promptText = """
        다음 제품 리뷰를 분석해주세요:
        
        리뷰 내용: {review}
        
        요구사항:
        1. sentiment는 positve, neutral, negative 중 하나로 응답하세요.
        2. score는 1점에서 10점 사이의 정수로 응답하세요.
        3. summary는 분석 내용을 한 문장으로 요약하세요.
        """;

    return clientBuilder.build()
        .prompt()
        .user(u -> u.text(promptText).param("review", review))
        .call()
        .entity(ProductAnalysisResponse.class);
  }

  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ProductAnalysisResponse {

    @JsonProperty("sentiment")
    String sentiment;

    @JsonProperty("score")
    int score;

    @JsonProperty("summary")
    String summary;

  }

}

