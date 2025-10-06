package com.sparta.dingdong.domain.category.dto;

import java.math.BigInteger;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MenuCategoryDto {

	@Data
	public static class Request {
		@NotBlank
		@Schema(example = "세트메뉴", description = "메뉴 카테고리명")
		private String name;

		@Schema(example = "1", description = "메뉴 카테고리 정렬 순서")
		private Integer sortMenuCategory;
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Response {
		@Schema(example = "d1a3b4cf-c378-4c55-b7da-123456789012", description = "메뉴 카테고리 UUID")
		private UUID id;

		@Schema(example = "e2b4c5df-c378-4c55-b7da-123456789012", description = "해당 카테고리가 속한 매장 UUID")
		private UUID storeId;

		@Schema(example = "세트메뉴", description = "메뉴 카테고리명")
		private String name;

		@Schema(example = "1", description = "메뉴 카테고리 정렬 순서")
		private Integer sortMenuCategory;
	}

	@Data
	public static class ItemRequest {
		@NotNull
		@Schema(example = "f3c5d6ef-c378-4c55-b7da-123456789012", description = "메뉴 아이템 UUID")
		private UUID menuItemId;

		@Schema(example = "1", description = "메뉴 아이템 정렬 순서")
		private Integer orderNo;
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ItemResponse {
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

		@Schema(example = "1", description = "메뉴 아이템 정렬 순서")
		private Integer orderNo;
	}
}