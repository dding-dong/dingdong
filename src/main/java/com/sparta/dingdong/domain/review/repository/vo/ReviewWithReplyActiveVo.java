package com.sparta.dingdong.domain.review.repository.vo;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ReviewWithReplyActiveVo {

	private final UUID reviewId;
	private final Long userId;
	private final UUID orderId;
	private final UUID storeId;
	private final Integer rating;
	private final String content;
	private final String imageUrl1;
	private final String imageUrl2;
	private final String imageUrl3;

	private final UUID replyId;
	private final Long ownerId;
	private final String replyContent;

	public ReviewWithReplyActiveVo(
		UUID reviewId,
		Long userId,
		UUID orderId,
		UUID storeId,
		Integer rating,
		String content,
		String imageUrl1,
		String imageUrl2,
		String imageUrl3,
		UUID replyId,
		Long ownerId,
		String replyContent) {
		this.reviewId = reviewId;
		this.userId = userId;
		this.orderId = orderId;
		this.storeId = storeId;
		this.rating = rating;
		this.content = content;
		this.imageUrl1 = imageUrl1;
		this.imageUrl2 = imageUrl2;
		this.imageUrl3 = imageUrl3;
		this.replyId = replyId;
		this.ownerId = ownerId;
		this.replyContent = replyContent;
	}
}
