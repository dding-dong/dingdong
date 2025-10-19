package com.sparta.dingdong.domain.user.dto.response;

import com.sparta.dingdong.domain.user.entity.Address;
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

	@Schema(description = "시 이름", example = "서울특별시")
	private String city;

	@Schema(description = "구 이름", example = "종로구")
	private String gu;

	@Schema(description = "동 이름", example = "연건동")
	private String dong;

	@Schema(description = "상세 주소", example = "101호")
	private String detailAddress;

	public static UserResponseDto of(User user, Address address) {
		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.phone(user.getPhone())
			.city(address.getDong().getGu().getCity().getName())
			.gu(address.getDong().getGu().getName())
			.dong(address.getDong().getName())
			.detailAddress(address.getDetailAddress())
			.build();
	}

	public static UserResponseDto from(User user) {
		Address address = user.getAddressList().stream()
			.filter(Address::isDefault)
			.findFirst()
			.orElse(user.getAddressList().get(0)); // 기본주소 없으면 첫번째
		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.phone(user.getPhone())
			.city(address.getDong().getGu().getCity().getName())
			.gu(address.getDong().getGu().getName())
			.dong(address.getDong().getName())
			.detailAddress(address.getDetailAddress())
			.build();
	}
}
