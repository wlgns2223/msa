package com.sparta.msa.lesson.domain.rag.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor // builder가 필요
@NoArgsConstructor // jackson 역직렬화가 필요
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionRequest {

  String question;
}
