package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;

public interface OwnerReviewService {

	void createReply(UUID reviewId, UserAuth userDetails, OwnerReviewDto.CreateReply request);

	void updateReply(UUID reviewId, UUID replyId, UserAuth userDetails, OwnerReviewDto.UpdateReply request);

	void deleteReply(UUID reviewId, UUID replyId, UserAuth userDetails);

	OwnerReviewDto.ReviewDetails getReviewDetails(UUID reviewId, UserAuth userDetails);

	List<OwnerReviewDto.StoreReviews> getOwnerReviews(UserAuth userDetails);

	OwnerReviewDto.StoreReviews getOwnerStoreReviews(UserAuth userDetails, UUID storeId);
}
