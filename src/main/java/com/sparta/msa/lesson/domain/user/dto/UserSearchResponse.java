package com.sparta.msa.lesson.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

// / GET /users <=== 유저 정보. 목록 조회. 다건조회
// 상세조회 DTO를 목록조회에 쓰면 응답자체가 너무 무겁다.
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchResponse {

  String name;

}
