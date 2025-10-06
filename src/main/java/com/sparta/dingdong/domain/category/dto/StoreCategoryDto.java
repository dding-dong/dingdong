package com.sparta.dingdong.domain.category.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StoreCategoryDto {

	@Data
	public static class Request {
		@NotBlank
		@Schema(example = "한식", description = "카테고리명")
		private String name;

		@Schema(example = "한식 전문점", description = "카테고리 설명")
		private String description;

		@Schema(example = "https://image.url/korean.jpg", description = "카테고리 이미지 URL")
		private String imageUrl;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Response {
		@Schema(example = "abb3b4cf-c378-4c55-b7da-123456789012", description = "카테고리 UUID")
		private UUID id;

		@Schema(example = "한식", description = "카테고리명")
		private String name;

		@Schema(example = "한식 전문점", description = "카테고리 설명")
		private String description;

		@Schema(example = "https://image.url/korean.jpg", description = "카테고리 이미지 URL")
		private String imageUrl;
	}
}