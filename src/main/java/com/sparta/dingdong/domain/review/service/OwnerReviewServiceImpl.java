package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.ReviewAlreadyRepliedException;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerReviewServiceImpl implements OwnerReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final UserService userService;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	@Override
	@Transactional
	public void createReply(UUID reviewId, UserAuth userDetails, OwnerReviewDto.CreateReply request) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		if (reviewReplyRepository.existsByReview(review)) {
			throw new ReviewAlreadyRepliedException(reviewId);
		}

		ReviewReply reply = ReviewReply.createReviewReply(review, user, request);

		reviewReplyRepository.save(reply);
	}
}
