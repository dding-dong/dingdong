package com.sparta.dingdong.domain.category.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryItemResponseDto {

	@Schema(example = "g4d6e7ff-c378-4c55-b7da-123456789012", description = "메뉴 카테고리 아이템 UUID")
	private UUID id;

	@Schema(example = "d1a3b4cf-c378-4c55-b7da-123456789012", description = "메뉴 카테고리 UUID")
	private UUID menuCategoryId;

	@Schema(example = "f3c5d6ef-c378-4c55-b7da-123456789012", description = "메뉴 아이템 UUID")
	private UUID menuItemId;

	@Schema(example = "세트버거", description = "메뉴 이름")
	private String menuItemName;

	@Schema(example = "5000", description = "메뉴 가격")
	private BigInteger menuItemPrice;

	@Schema(example = "1", description = "메뉴 카테고리 내 메뉴 아이템 정렬 순서")
	private Integer orderNo;
}
