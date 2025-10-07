package com.sparta.dingdong.domain.store.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매장 등록 요청 DTO")
public class StoreRequestDto {

	@NotBlank
	@Schema(example = "김밥천국", description = "매장 이름")
	private String name;

	@Schema(example = "분식 전문점", description = "매장 설명")
	private String description;

	@NotBlank
	@Schema(example = "서울특별시 강남구 테헤란로 123", description = "매장 주소")
	private String address;

	@NotBlank
	@Schema(example = "06234", description = "우편번호")
	private String postalCode;

	@NotNull
	@Schema(example = "abb3b4cf-c378-4c55-b7da-123456789012", description = "가게카테고리 UUID")
	private UUID storeCategoryId;

	@Schema(example = "[\"강남구\", \"서초구\"]", description = "배달 가능 지역 목록")
	private List<String> deliveryAreaIds;
}