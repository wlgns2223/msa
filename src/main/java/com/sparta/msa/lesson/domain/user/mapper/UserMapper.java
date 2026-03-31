package com.sparta.msa.lesson.domain.user.mapper;

import com.sparta.msa.lesson.domain.user.dto.request.UserRequest;
import com.sparta.msa.lesson.domain.user.dto.response.UserResponse;
import com.sparta.msa.lesson.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toUserResponse(User user);

  //  User toEntity(UserRequest userRequest);
  @Mapping(target = "password", source = "encodePassword")
  User toEntity(UserRequest userRequest, String encodePassword);
}
