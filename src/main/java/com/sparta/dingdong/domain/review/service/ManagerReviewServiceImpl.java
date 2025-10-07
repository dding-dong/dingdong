package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.review.dto.response.ManagerReviewResponseDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewQueryRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.review.repository.vo.ManagerSearchReviewVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerReviewServiceImpl implements ManagerReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewQueryRepository reviewQueryRepository;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	@Override
	@Transactional
	public void hideReview(UUID reviewId) {
		Review review = findReview(reviewId);

		review.hide();
	}

	@Override
	@Transactional(readOnly = true)
	public ManagerReviewResponseDto getReviewDetails(UUID reviewId) {
		Review review = findReview(reviewId);
		return ManagerReviewResponseDto.from(review);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ManagerReviewResponseDto> getReviews() {
		// 모든 리뷰 조회
		List<Review> reviews = reviewRepository.findAll();

		return reviews.stream()
			.map(ManagerReviewResponseDto::from)
			.toList();
	}

	@Override
	public Page<ManagerReviewResponseDto> getSearchReviews(Long userId, Long ownerId, UUID storeId, UUID orderId,
		Integer rating, String keyword, Pageable pageable) {
		Page<ManagerSearchReviewVo> reviewPage = reviewQueryRepository.searchReviewsAll(userId, ownerId, storeId,
			orderId, rating, keyword, pageable);
		return reviewPage.map(ManagerReviewResponseDto::from);
	}
}
