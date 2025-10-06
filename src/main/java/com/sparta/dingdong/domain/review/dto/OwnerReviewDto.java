package com.sparta.dingdong.domain.review.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
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
		private OwnerReviewDto.ReviewReplyDetails reply;
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
		private Integer rating;
		private String content;
		private String imageUrl1;
		private String imageUrl2;
		private String imageUrl3;
		private ReviewReply reply;
		private Boolean isDisplayed;
	}

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class StoreReviews {
		private UUID storeId;
		private String storeName;
		private List<Review> reviews;
	}
}
