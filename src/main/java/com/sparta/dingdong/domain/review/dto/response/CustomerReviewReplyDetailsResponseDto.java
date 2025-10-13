package com.sparta.dingdong.domain.review.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerReviewReplyDetailsResponseDto {
	private UUID replyId;
	private Long ownerId;
	private String content;
	private Boolean isDisplayed;
}
