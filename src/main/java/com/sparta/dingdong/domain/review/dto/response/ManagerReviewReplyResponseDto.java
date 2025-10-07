package com.sparta.dingdong.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.dingdong.domain.review.entity.ReviewReply;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerReviewReplyResponseDto {
	private UUID replyId;
	private Long ownerId;
	private String content;
	private Boolean isDisplayed;
	private LocalDateTime createdAt;
	private Long createdBy;
	private LocalDateTime updatedAt;
	private Long updatedBy;
	private LocalDateTime deletedAt;
	private Long deletedBy;

	public static ManagerReviewReplyResponseDto from(ReviewReply reply) {
		if (reply == null) {
			return null;
		}
		return ManagerReviewReplyResponseDto.builder()
			.replyId(reply.getId())
			.ownerId(reply.getOwner().getId())
			.content(reply.getContent())
			.isDisplayed(reply.isDisplayed())
			.createdAt(reply.getCreatedAt())
			.createdBy(reply.getCreatedBy())
			.updatedAt(reply.getUpdatedAt())
			.updatedBy(reply.getUpdatedBy())
			.deletedAt(reply.getDeletedAt())
			.deletedBy(reply.getDeletedBy())
			.build();
	}

	public static ManagerReviewReplyResponseDto from(UUID replyId, Long ownerId, String replyContent,
		Boolean isReplyDisplayed, LocalDateTime replyCreatedAt, Long replyCreatedBy, LocalDateTime replyUpdatedAt,
		Long replyUpdatedBy, LocalDateTime replyDeletedAt, Long replyDeletedBy) {

		if (replyId == null) {
			return null;
		}

		return ManagerReviewReplyResponseDto.builder()
			.replyId(replyId)
			.ownerId(ownerId)
			.content(replyContent)
			.isDisplayed(isReplyDisplayed)
			.createdAt(replyCreatedAt)
			.createdBy(replyCreatedBy)
			.updatedAt(replyUpdatedAt)
			.updatedBy(replyUpdatedBy)
			.deletedAt(replyDeletedAt)
			.deletedBy(replyDeletedBy)
			.build();
	}
}
