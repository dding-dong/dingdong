package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import com.sparta.dingdong.domain.review.dto.CommonReviewDto;

public interface CommonReviewService {
	CommonReviewDto.ReviewDetails selectReview(UUID reviewId);
}
