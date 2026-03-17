package com.sparta.msa.lesson.domain.user.controller;

import com.sparta.msa.lesson.domain.user.dto.UserSearchResponse;
import com.sparta.msa.lesson.domain.user.dto.request.UserRequest;
import com.sparta.msa.lesson.domain.user.dto.response.UserResponse;
import com.sparta.msa.lesson.domain.user.service.UserService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<UserResponse> create(@Valid @RequestBody UserRequest request) {
    return ApiResponse.ok(userService.create(request));
  }

  @GetMapping
  public ApiResponse<List<UserSearchResponse>> getAllUsers() {
    return ApiResponse.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ApiResponse<UserResponse> getById(@PathVariable Long id) {
    return ApiResponse.ok(userService.getUserById(id));
  }

  @PutMapping("/{id}")
  public ApiResponse<UserResponse> update(@PathVariable Long id,
      @Valid @RequestBody UserRequest request) {
    return ApiResponse.ok(userService.update(id, request));
  }
}
