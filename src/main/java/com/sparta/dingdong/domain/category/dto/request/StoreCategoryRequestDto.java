package com.sparta.dingdong.domain.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StoreCategoryRequestDto {

	@NotBlank
	@Schema(example = "한식", description = "가게카테고리명")
	private String name;

	@Schema(example = "한식 전문점", description = "가게카테고리 설명")
	private String description;

	@Schema(example = "https://image.url/korean.jpg", description = "가게카테고리 이미지 URL")
	private String imageUrl;
}