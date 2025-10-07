package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.review.dto.response.CommonReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.CommonReviewResponseDto;

public interface CommonReviewService {
	CommonReviewDetailsResponseDto selectReview(UUID reviewId);

	List<CommonReviewResponseDto> selectReviews();
}
