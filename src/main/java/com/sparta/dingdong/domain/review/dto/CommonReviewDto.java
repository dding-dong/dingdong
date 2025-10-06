package com.sparta.dingdong.domain.review.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

public class CommonReviewDto {

	@Getter
	@Builder
	public static class ReviewReplyDetails {
		private UUID replyId;
		private Long ownerId;
		private String content;
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
		private CommonReviewDto.ReviewReplyDetails reply;
	}
}
