package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;

public interface CustomerReviewService {
	void createReview(UUID orderId, UserAuth userDetails, CustomerReviewDto.CreateReview request);

	void updateReview(UUID reviewId, UserAuth userDetails, CustomerReviewDto.UpdateReview request);

	void deleteReview(UUID reviewId, UserAuth userDetails);

	CustomerReviewDto.ReviewDetails selectReviewDetails(UUID reviewId, UserAuth userDetails);

	List<CustomerReviewDto.Review> selectActiveReviews(UserAuth userDetails);
}
