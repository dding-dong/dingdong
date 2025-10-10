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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "인증 API", description = "인증 관련 API입니다.")
public class AuthControllerV1 {

	private final AuthService authService;

	@Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 로그인하고, AccessToken과 RefreshToken을 반환합니다.")
	@PostMapping("/login")
	public ResponseEntity<BaseResponseDto<TokenResponse>> login(@Valid @RequestBody LoginRequestDto req) {
		TokenResponse token = authService.login(req);
		return ResponseEntity.ok(BaseResponseDto.success("로그인 성공", token));
	}

	@Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다.")
	@PostMapping("/logout")
	public ResponseEntity<BaseResponseDto<Void>> logout(HttpServletRequest req) {
		authService.logout(req);
		return ResponseEntity.ok(BaseResponseDto.success("로그아웃 성공"));
	}

	@Operation(summary = "토큰 재발급", description = "RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급합니다.")
	@PostMapping("/reissue")
	public ResponseEntity<BaseResponseDto<TokenResponse>> reissue(@RequestHeader("Authorization") String refreshToken) {
		TokenResponse token = authService.reissue(refreshToken);
		return ResponseEntity.ok(BaseResponseDto.success("토큰 재발급 성공", token));
	}
}
