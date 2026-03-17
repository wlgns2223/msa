package com.sparta.msa.lesson.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserRequest {

  @NotBlank
  @NotNull
  String name;

  @NotNull
  @Email
  String email;

  @Min(4)
  @NotNull
  String password;

}
