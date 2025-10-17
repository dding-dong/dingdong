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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Tag(name = "유저 API", description = "유저 관련 API입니다.")
public class UserControllerV1 {

	private final UserServiceImpl userServiceImpl;

	@Operation(summary = "회원가입", description = "신규 사용자를 생성합니다. 이메일, 비밀번호, 이름, 닉네임 등을 입력받습니다.")
	@PostMapping("/signup")
	public ResponseEntity<BaseResponseDto<Void>> signup(@Valid @RequestBody UserCreateRequestDto req) {
		userServiceImpl.createUser(req);
		return ResponseEntity.ok(BaseResponseDto.success("회원가입 완료"));
	}

	@Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@GetMapping("/me")
	public ResponseEntity<BaseResponseDto<UserResponseDto>> findById(@AuthenticationPrincipal UserAuth userAuth) {
		UserResponseDto res = userServiceImpl.findById(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 조회 성공", res));
	}

	@Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다. 닉네임, 비밀번호, 전화번호, 주소 등을 변경할 수 있습니다.")
	@PatchMapping
	public ResponseEntity<BaseResponseDto<Void>> updateUser(@Valid @RequestBody UserUpdateRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		userServiceImpl.updateUser(req, userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 정보 수정 완료"));
	}

	@Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리합니다.")
	@DeleteMapping
	public ResponseEntity<BaseResponseDto<Void>> deleteUser(@AuthenticationPrincipal UserAuth userAuth) {
		userServiceImpl.deleteUser(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("회원 탈퇴 완료"));
	}
}
