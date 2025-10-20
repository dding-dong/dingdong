package com.sparta.dingdong.domain.user.dto.response;

import com.sparta.dingdong.domain.user.entity.Address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponseDto {

	@Schema(description = "주소 ID", example = "1")
	private Long id;

	@Schema(description = "시 이름", example = "서울특별시")
	private String city;

	@Schema(description = "구 이름", example = "종로구")
	private String gu;

	@Schema(description = "동 이름", example = "연건동")
	private String dong;

	@Schema(description = "상세 주소", example = "101호")
	private String detailAddress;

	@Schema(description = "우편번호", example = "06210")
	private String postalCode;

	@Schema(description = "기본 주소 여부", example = "true")
	private boolean isDefault;

	public static AddressResponseDto of(Address address) {
		return AddressResponseDto.builder()
			.id(address.getId())
			.city(address.getDong().getGu().getCity().getName())
			.gu(address.getDong().getGu().getName())
			.dong(address.getDong().getName())
			.detailAddress(address.getDetailAddress())
			.postalCode(address.getPostalCode())
			.isDefault(address.isDefault())
			.build();
	}
}