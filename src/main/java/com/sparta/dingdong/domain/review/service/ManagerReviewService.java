package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import com.sparta.dingdong.domain.review.dto.ManagerReviewDto;

public interface ManagerReviewService {
	void hideReview(UUID reviewId);

	ManagerReviewDto.Review getReviewDetails(UUID reviewId);
}
