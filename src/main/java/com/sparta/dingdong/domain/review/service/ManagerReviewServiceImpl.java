package com.sparta.dingdong.domain.review.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.review.dto.ManagerReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerReviewServiceImpl implements ManagerReviewService {

	private final ReviewRepository reviewRepository;

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
	public ManagerReviewDto.Review getReviewDetails(UUID reviewId) {
		Review review = findReview(reviewId);

		ManagerReviewDto.ReviewReply replyDto = Optional.ofNullable(review.getReviewReply())
			.map(reply -> ManagerReviewDto.ReviewReply.builder()
				.replyId(reply.getId())
				.ownerId(reply.getOwner().getId())
				.content(reply.getContent())
				.isDisplayed(reply.isDisplayed())
				.build())
			.orElse(null);

		ManagerReviewDto.Review reviewDto = ManagerReviewDto.Review.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.orderId(review.getOrder().getId())
			.storeId(review.getStore().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.imageUrl1(review.getImageUrl1())
			.imageUrl2(review.getImageUrl2())
			.imageUrl3(review.getImageUrl3())
			.reply(replyDto)
			.isDisplayed(review.isDisplayed())
			.build();

		return reviewDto;
	}
}
