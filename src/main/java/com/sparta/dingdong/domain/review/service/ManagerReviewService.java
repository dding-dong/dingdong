package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.review.dto.response.ManagerReviewResponseDto;

public interface ManagerReviewService {
	void hideReview(UUID reviewId);

	ManagerReviewResponseDto getReviewDetails(UUID reviewId);

	List<ManagerReviewResponseDto> getReviews();

	Page<ManagerReviewResponseDto> getSearchReviews(Long userId, Long ownerId, UUID storeId, UUID orderId,
		Integer rating, String keyword, Pageable pageable);
}
