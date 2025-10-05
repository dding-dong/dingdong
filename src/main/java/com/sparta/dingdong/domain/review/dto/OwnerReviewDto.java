package com.sparta.dingdong.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class OwnerReviewDto {

	@Getter
	public static class CreateReply {

		@NotNull
		@Schema(description = "리뷰 답장 내용", example = "감사합니다!", required = true)
		private String content;

		@Schema(description = "리뷰 딥장 노출 여부", defaultValue = "true")
		private boolean isDisplayed = true;
	}

	@Getter
	public static class UpdateReply {

		@Schema(description = "리뷰 답장 내용", example = "감사합니다!")
		private String content;

		@Schema(description = "리뷰 딥장 노출 여부")
		private boolean isDisplayed = true;
	}
}
