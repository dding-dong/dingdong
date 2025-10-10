package com.sparta.dingdong.domain.user.dto.request;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {

	@Schema(description = "사용자 이메일", example = "user@example.com")
	@NotBlank
	@Email
	private String email;

	@Schema(description = "비밀번호 (대소문자, 숫자, 특수문자 포함 8자 이상)", example = "Password123!")
	@NotBlank
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
		message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	private String password;

	@Schema(description = "사용자 이름", example = "홍길동")
	@NotBlank
	private String username;

	@Schema(description = "사용자 닉네임", example = "길동이")
	@NotBlank
	private String nickname;

	@Schema(description = "사용자 연락처", example = "010-1234-5678")
	@NotBlank
	private String phone;

	@Schema(description = "사용자 권한", example = "CUSTOMER")
	@NotNull
	private UserRole userRole;

	// 드롭다운 방식으로 시/구/동 선택
	@Schema(description = "시 코드", example = "02")
	@NotBlank
	private String cityId; // ex: "02" 서울특별시

	@Schema(description = "구 코드", example = "23")
	@NotBlank
	private String guId;   // ex: "23" 종로구

	@Schema(description = "동 코드", example = "400")
	@NotBlank
	private String dongId; // ex: "400" 연건동

	@Schema(description = "상세 주소", example = "101호")
	@NotBlank
	private String detailAddress;  // 상세주소 (사용자가 입력)

	@Schema(description = "우편번호", example = "06210")
	@NotBlank
	private String postalCode;     // 우편번호
}
