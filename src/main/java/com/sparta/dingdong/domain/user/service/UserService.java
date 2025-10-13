package com.sparta.dingdong.domain.user.service;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.request.UserUpdateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.entity.User;

@Service
public interface UserService {
	void createUser(UserCreateRequestDto requestDto);

	void updateUser(UserUpdateRequestDto request, UserAuth userAuth);

	void checkPassword(String rawPassword, String hashedPassword);

	void deleteUser(UserAuth userAuth);

	UserResponseDto findById(UserAuth userAuth);

	User findByUser(UserAuth userAuth);

	User findByUser(Long userId);
}
