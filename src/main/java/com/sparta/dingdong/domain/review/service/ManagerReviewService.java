package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.review.dto.response.ManagerReviewResponseDto;

public interface ManagerReviewService {
	void hideReview(UUID reviewId);

	ManagerReviewResponseDto getReviewDetails(UUID reviewId);

	List<ManagerReviewResponseDto> getReviews();
}
