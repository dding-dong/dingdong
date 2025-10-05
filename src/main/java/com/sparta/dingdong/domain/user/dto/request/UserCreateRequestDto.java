package com.sparta.dingdong.domain.user.dto.request;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String username;

	@NotBlank
	private String nickname;

	@NotBlank
	private String phone;

	@NotNull
	private UserRole userRole;

	// 드롭다운 방식으로 시/구/동 선택
	@NotBlank
	private String cityId; // ex: "02" 서울특별시

	@NotBlank
	private String guId;   // ex: "23" 종로구

	@NotBlank
	private String dongId; // ex: "400" 연건동

	@NotBlank
	private String detailAddress;  // 상세주소 (사용자가 입력)

	@NotBlank
	private String postalCode;     // 우편번호
}
