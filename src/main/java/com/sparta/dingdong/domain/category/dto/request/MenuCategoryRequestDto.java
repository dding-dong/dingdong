package com.sparta.dingdong.domain.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuCategoryRequestDto {

	@NotBlank
	@Schema(example = "메인 메뉴", description = "메뉴카테고리명")
	private String name;

	@Schema(example = "1", description = "메뉴카테고리 정렬 순서")
	private Integer sortMenuCategory;
}