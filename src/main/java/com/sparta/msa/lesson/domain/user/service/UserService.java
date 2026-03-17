package com.sparta.msa.lesson.domain.user.service;

import com.sparta.msa.lesson.domain.user.dto.UserSearchResponse;
import com.sparta.msa.lesson.domain.user.dto.request.UserRequest;
import com.sparta.msa.lesson.domain.user.dto.response.UserResponse;
import com.sparta.msa.lesson.domain.user.entity.User;
import com.sparta.msa.lesson.domain.user.repository.UserRepository;
import com.sparta.msa.lesson.global.enums.DomainExceptionCode;
import com.sparta.msa.lesson.global.exception.DomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public UserResponse create(UserRequest request) {
    return UserResponse.builder().build();
  }

  public List<UserSearchResponse> getAllUsers() {
    return List.of();
  }

  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND));
    return UserResponse.builder().build();
  }

  public UserResponse update(Long id, UserRequest request) {
    return UserResponse.builder().build();
  }

}
