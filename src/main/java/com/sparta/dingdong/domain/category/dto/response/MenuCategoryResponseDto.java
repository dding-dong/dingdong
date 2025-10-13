package com.sparta.dingdong.domain.category.dto.response;

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
public class MenuCategoryResponseDto {

	@Schema(example = "d1a3b4cf-c378-4c55-b7da-123456789012", description = "메뉴 카테고리 UUID")
	private UUID id;

	@Schema(example = "e2b4c5df-c378-4c55-b7da-123456789012", description = "해당 메뉴 카테고리가 속한 매장 UUID")
	private UUID storeId;

	@Schema(example = "메인 메뉴", description = "메뉴 카테고리명")
	private String name;

	@Schema(example = "1", description = "메뉴 카테고리 정렬 순서")
	private Integer sortMenuCategory;
}
