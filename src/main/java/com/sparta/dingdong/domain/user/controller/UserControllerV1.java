package com.sparta.dingdong.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.request.UserUpdateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.service.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserControllerV1 {

	private final UserServiceImpl userServiceImpl;

	@PostMapping("/signup")
	public ResponseEntity<BaseResponseDto<Void>> signup(
		@Valid @RequestBody UserCreateRequestDto req) {
		userServiceImpl.createUser(req);
		return ResponseEntity.ok(BaseResponseDto.success("회원가입 완료"));
	}

	@GetMapping("/me")
	public ResponseEntity<BaseResponseDto<UserResponseDto>> findById(@AuthenticationPrincipal UserAuth userAuth) {
		UserResponseDto res = userServiceImpl.findById(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 조회 성공", res));
	}

	@PatchMapping
	public ResponseEntity<BaseResponseDto<Void>> updateUser(@Valid @RequestBody UserUpdateRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		userServiceImpl.updateUser(req, userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 정보 수정 완료"));
	}

	@DeleteMapping
	public ResponseEntity<BaseResponseDto<Void>> deleteUser(@AuthenticationPrincipal UserAuth userAuth) {
		userServiceImpl.deleteUser(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 탈퇴 완료"));
	}
}