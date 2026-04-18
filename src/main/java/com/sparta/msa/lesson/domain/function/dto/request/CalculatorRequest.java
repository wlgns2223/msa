package com.sparta.msa.lesson.domain.function.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalculatorRequest {

  double a;
  double b;
  String operation;


}
