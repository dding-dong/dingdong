package com.sparta.dingdong.domain.category.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuCategoryItemRequestDto {

	@NotNull
	@Schema(example = "f3c5d6ef-c378-4c55-b7da-123456789012", description = "메뉴아이템 UUID")
	private UUID menuItemId;

	@Schema(example = "1", description = "메뉴아이템 정렬 순서")
	private Integer orderNo;
}