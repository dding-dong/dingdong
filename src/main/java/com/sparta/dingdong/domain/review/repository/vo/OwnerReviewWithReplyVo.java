package com.sparta.dingdong.domain.review.repository.vo;

import java.util.UUID;

import lombok.Getter;

@Getter
public class OwnerReviewWithReplyVo {

	private final UUID storeId;
	private final String storeName;

	private final UUID reviewId;
	private final Long userId;
	private final Integer rating;
	private final String content;
	private final String imageUrl1;
	private final String imageUrl2;
	private final String imageUrl3;
	private final Boolean isReviewDisplayed;

	private final UUID replyId;
	private final Long ownerId;
	private final String replyContent;
	private final Boolean isReviewReplyDisplayed;

	public OwnerReviewWithReplyVo(
		UUID storeId,
		String storeName,
		UUID reviewId,
		Long userId,
		Integer rating,
		String content,
		String imageUrl1,
		String imageUrl2,
		String imageUrl3,
		Boolean isReviewDisplayed,
		UUID replyId,
		Long ownerId,
		String replyContent,
		Boolean isReviewReplyDisplayed
	) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.reviewId = reviewId;
		this.userId = userId;
		this.rating = rating;
		this.content = content;
		this.imageUrl1 = imageUrl1;
		this.imageUrl2 = imageUrl2;
		this.imageUrl3 = imageUrl3;
		this.isReviewDisplayed = isReviewDisplayed;
		this.replyId = replyId;
		this.ownerId = ownerId;
		this.replyContent = replyContent;
		this.isReviewReplyDisplayed = isReviewReplyDisplayed;
	}
}
