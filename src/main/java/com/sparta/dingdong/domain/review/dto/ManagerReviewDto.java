package com.sparta.dingdong.domain.review.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

public class ManagerReviewDto {

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
		private ManagerReviewDto.ReviewReply reply;
		private Boolean isDisplayed;
	}
}
