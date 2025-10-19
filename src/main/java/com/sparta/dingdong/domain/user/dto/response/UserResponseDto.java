package com.sparta.dingdong.domain.user.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.sparta.dingdong.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

	@Schema(description = "사용자 ID", example = "1")
	private Long id;

	@Schema(description = "사용자 이메일", example = "user@example.com")
	private String email;

	@Schema(description = "사용자 이름", example = "홍길동")
	private String username;

	@Schema(description = "사용자 닉네임", example = "길동이")
	private String nickname;

	@Schema(description = "사용자 연락처", example = "010-1234-5678")
	private String phone;
	
	@Schema(description = "주소 목록")
	private List<AddressResponseDto> addressList;

	public static UserResponseDto from(User user) {
		List<AddressResponseDto> addressDtoList = user.getAddressList().stream()
			.map(AddressResponseDto::of)
			.collect(Collectors.toList());

		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.phone(user.getPhone())
			.addressList(addressDtoList)
			.build();
	}
}
