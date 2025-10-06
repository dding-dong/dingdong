package com.sparta.dingdong.domain.review.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class CustomerReviewDto {

	@Getter
	public static class CreateReview {

		@Schema(description = "별점 (1~5)", example = "5", required = true)
		@NotNull
		private Integer rating;

		@Schema(description = "리뷰 내용", example = "맛있어요!", required = true)
		@NotBlank
		private String content;

		@Schema(description = "이미지 URL 1", example = "https://example.com/image1.jpg")
		private String imageUrl1;

		@Schema(description = "이미지 URL 2", example = "https://example.com/image2.jpg")
		private String imageUrl2;

		@Schema(description = "이미지 URL 3", example = "https://example.com/image3.jpg")
		private String imageUrl3;

		@Schema(description = "리뷰 노출 여부", defaultValue = "true")
		private boolean isDisplayed = true;
	}

	@Getter
	public static class UpdateReview {

		@Schema(description = "별점 (1~5)", example = "5")
		private Integer rating;

		@Schema(description = "리뷰 내용", example = "맛있어요!")
		private String content;

		@Schema(description = "이미지 URL 1", example = "https://example.com/image1.jpg")
		private String imageUrl1;

		@Schema(description = "이미지 URL 2", example = "https://example.com/image2.jpg")
		private String imageUrl2;

		@Schema(description = "이미지 URL 3", example = "https://example.com/image3.jpg")
		private String imageUrl3;

		@Schema(description = "리뷰 노출 여부")
		private boolean isDisplayed;
	}

	@Getter
	@Builder
	public static class ReviewReplyDetails {
		private UUID replyId;
		private Long ownerId;
		private String content;
		private Boolean isDisplayed;
	}

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ReviewDetails {
		private UUID reviewId;
		private Long userId;
		private UUID orderId;
		private UUID storeId;
		private Integer rating;
		private String content;
		private String imageUrl1;
		private String imageUrl2;
		private String imageUrl3;
		private CustomerReviewDto.ReviewReplyDetails reply;
		private Boolean isDisplayed;
	}

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ReviewReply {
		private UUID replyId;
		private Long ownerId;
		private String content;
		private Boolean isDisplayed;
	}

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Review {
		private UUID reviewId;
		private Long userId;
		private UUID orderId;
		private UUID storeId;
		private Integer rating;
		private String content;
		private String imageUrl1;
		private String imageUrl2;
		private String imageUrl3;
		private CustomerReviewDto.ReviewReply reply;
		private Boolean isDisplayed;
	}
}
