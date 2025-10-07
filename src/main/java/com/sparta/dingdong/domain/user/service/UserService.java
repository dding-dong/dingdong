package com.sparta.dingdong.domain.user.service;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.entity.User;

@Service
public interface UserService {
	UserResponseDto createUser(UserCreateRequestDto requestDto);

	User findByUser(UserAuth userAuth);
}
