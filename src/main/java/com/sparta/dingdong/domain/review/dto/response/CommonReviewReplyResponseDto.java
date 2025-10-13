package com.sparta.dingdong.domain.review.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonReviewReplyResponseDto {
	private UUID replyId;
	private Long ownerId;
	private String content;
}
