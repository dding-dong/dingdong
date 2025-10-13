package com.sparta.dingdong.domain.review.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonReviewReplyDetailsResponseDto {
	private UUID replyId;
	private Long ownerId;
	private String content;
}
