package com.sparta.dingdong.domain.user.dto.response;

import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
	private Long id;
	private String email;
	private String nickname;
	private String phone;
	private String city;
	private String gu;
	private String dong;
	private String detailAddress;

	public static UserResponseDto of(User user, Address address) {
		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.phone(user.getPhone())
			.city(address.getDong().getGu().getCity().getName())
			.gu(address.getDong().getGu().getName())
			.dong(address.getDong().getName())
			.detailAddress(address.getDetailAddress())
			.build();
	}

	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.phone(user.getPhone())
			.city(user.getAddressList().get(0).getDong().getGu().getCity().toString())
			.gu(user.getAddressList().get(0).getDong().getGu().toString())
			.dong(user.getAddressList().get(0).getDong().toString())
			.detailAddress(user.getAddressList().get(0).getDetailAddress())
			.build();
	}
}
