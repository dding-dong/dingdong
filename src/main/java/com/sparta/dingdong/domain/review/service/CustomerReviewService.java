package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.request.CustomerCreateReviewRequestDto;
import com.sparta.dingdong.domain.review.dto.request.CustomerUpdateReviewRequestDto;
import com.sparta.dingdong.domain.review.dto.response.CustomerReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.CustomerReviewResponseDto;

public interface CustomerReviewService {
	void createReview(UUID orderId, UserAuth userDetails, CustomerCreateReviewRequestDto request);

	void updateReview(UUID reviewId, UserAuth userDetails, CustomerUpdateReviewRequestDto request);

	void deleteReview(UUID reviewId, UserAuth userDetails);

	CustomerReviewDetailsResponseDto selectReviewDetails(UUID reviewId, UserAuth userDetails);

	List<CustomerReviewResponseDto> selectActiveReviews(UserAuth userDetails);
}
