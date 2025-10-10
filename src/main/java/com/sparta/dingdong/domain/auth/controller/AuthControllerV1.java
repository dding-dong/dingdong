package com.sparta.dingdong.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.auth.dto.request.LoginRequestDto;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthControllerV1 {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<BaseResponseDto<TokenResponse>> login(@Valid @RequestBody LoginRequestDto req) {
		return ResponseEntity.ok(BaseResponseDto.success("로그인 성공", authService.login(req)));
	}

	@PostMapping("/logout")
	public ResponseEntity<BaseResponseDto<Void>> logout(HttpServletRequest req) {
		authService.logout(req);
		return ResponseEntity.ok(BaseResponseDto.success("로그아웃 성공"));
	}

	@PostMapping("/reissue")
	public ResponseEntity<BaseResponseDto<TokenResponse>> reissue(@RequestHeader("Authorization") String refreshToken) {
		return ResponseEntity.ok(BaseResponseDto.success("토큰 재발급 성공", authService.reissue(refreshToken)));
	}
}