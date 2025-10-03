package com.sparta.dingdong.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.service.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class UserController {

	private final UserServiceImpl userServiceImpl;

	@PostMapping("/signup")
	public ResponseEntity<BaseResponseDto<UserResponseDto>> signup(
		@Valid @RequestBody UserCreateRequestDto requestDto) {
		UserResponseDto responseDto = userServiceImpl.createUser(requestDto);
		return ResponseEntity.ok(BaseResponseDto.success("회원가입 완료", responseDto));
	}
}