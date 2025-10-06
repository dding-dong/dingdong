package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.domain.review.dto.CommonReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonReviewServiceImpl implements CommonReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	public ReviewReply findActiveReviewReply(Review review) {
		return reviewReplyRepository.findByReviewAndDeletedAtIsNullAndDeletedByIsNullAndIsDisplayedTrue(review)
			.orElse(null);
	}

	@Override
	public CommonReviewDto.ReviewDetails selectReview(UUID reviewId) {
		Review review = findReview(reviewId);

		// 고객은 삭제 및 비활성화된 리뷰는 볼 수 없다.
		if (!review.isActive()) {
			throw new ReviewNotFoundException();
		}

		ReviewReply reviewReply = findActiveReviewReply(review);
		CommonReviewDto.ReviewReplyDetails reviewReplyDetails = null;

		// 달린 리뷰가 있거나, 활성화 된 리뷰면 reviewReplyDetails에 담을 수 있다.
		if (reviewReply != null) {
			reviewReplyDetails = CommonReviewDto.ReviewReplyDetails.builder()
				.replyId(reviewReply.getId())
				.ownerId(reviewReply.getOwner().getId())
				.content(reviewReply.getContent())
				.build();
		}

		CommonReviewDto.ReviewDetails reviewDetails = CommonReviewDto.ReviewDetails.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.orderId(review.getOrder().getId())
			.storeId(review.getStore().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.imageUrl1(review.getImageUrl1())
			.imageUrl2(review.getImageUrl2())
			.imageUrl3(review.getImageUrl3())
			.reply(reviewReplyDetails)
			.build();

		return reviewDetails;
	}
}
