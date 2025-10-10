package com.sparta.dingdong.domain.review.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerReviewDetailsResponseDto {
	private UUID reviewId;
	private Long userId;
	private UUID orderId;
	private UUID storeId;
	private Integer rating;
	private String content;
	private String imageUrl1;
	private String imageUrl2;
	private String imageUrl3;
	private OwnerReviewReplyDetailsResponseDto reply;
	private Boolean isDisplayed;
}
