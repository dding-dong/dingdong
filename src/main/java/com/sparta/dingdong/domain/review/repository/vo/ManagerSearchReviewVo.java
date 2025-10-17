package com.sparta.dingdong.domain.review.repository.vo;

import java.time.LocalDateTime;
import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.ToString;

/**
 * 매니저 리뷰 검색 결과 VO
 * - Review + ReviewReply 통합 정보
 */
@Getter
@ToString
public class ManagerSearchReviewVo {

	//Review 정보
	private final UUID reviewId;
	private final Long userId;
	private final UUID orderId;
	private final UUID storeId;
	private final Integer rating;
	private final String content;
	private final String imageUrl1;
	private final String imageUrl2;
	private final String imageUrl3;
	private final Boolean isReviewDisplayed;

	private final LocalDateTime reviewCreatedAt;
	private final Long reviewCreatedBy;
	private final LocalDateTime reviewUpdatedAt;
	private final Long reviewUpdatedBy;
	private final LocalDateTime reviewDeletedAt;
	private final Long reviewDeletedBy;

	// Reply 정보
	private final UUID replyId;
	private final Long ownerId;
	private final String replyContent;
	private final Boolean isReplyDisplayed;

	private final LocalDateTime replyCreatedAt;
	private final Long replyCreatedBy;
	private final LocalDateTime replyUpdatedAt;
	private final Long replyUpdatedBy;
	private final LocalDateTime replyDeletedAt;
	private final Long replyDeletedBy;

	@QueryProjection
	public ManagerSearchReviewVo(UUID reviewId, Long userId, UUID orderId, UUID storeId, Integer rating, String content,
		String imageUrl1, String imageUrl2, String imageUrl3, Boolean isReviewDisplayed, LocalDateTime reviewCreatedAt,
		Long reviewCreatedBy, LocalDateTime reviewUpdatedAt, Long reviewUpdatedBy, LocalDateTime reviewDeletedAt,
		Long reviewDeletedBy, UUID replyId, Long ownerId, String replyContent, Boolean isReplyDisplayed,
		LocalDateTime replyCreatedAt, Long replyCreatedBy, LocalDateTime replyUpdatedAt, Long replyUpdatedBy,
		LocalDateTime replyDeletedAt, Long replyDeletedBy) {
		this.reviewId = reviewId;
		this.userId = userId;
		this.orderId = orderId;
		this.storeId = storeId;
		this.rating = rating;
		this.content = content;
		this.imageUrl1 = imageUrl1;
		this.imageUrl2 = imageUrl2;
		this.imageUrl3 = imageUrl3;
		this.isReviewDisplayed = isReviewDisplayed;
		this.reviewCreatedAt = reviewCreatedAt;
		this.reviewCreatedBy = reviewCreatedBy;
		this.reviewUpdatedAt = reviewUpdatedAt;
		this.reviewUpdatedBy = reviewUpdatedBy;
		this.reviewDeletedAt = reviewDeletedAt;
		this.reviewDeletedBy = reviewDeletedBy;
		this.replyId = replyId;
		this.ownerId = ownerId;
		this.replyContent = replyContent;
		this.isReplyDisplayed = isReplyDisplayed;
		this.replyCreatedAt = replyCreatedAt;
		this.replyCreatedBy = replyCreatedBy;
		this.replyUpdatedAt = replyUpdatedAt;
		this.replyUpdatedBy = replyUpdatedBy;
		this.replyDeletedAt = replyDeletedAt;
		this.replyDeletedBy = replyDeletedBy;
	}
}
