package com.sparta.msa.lesson.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductStockRequest {

  @NotNull
  @PositiveOrZero // 값을 양수로 혹은 0으로 제한
  Integer stock;

}
