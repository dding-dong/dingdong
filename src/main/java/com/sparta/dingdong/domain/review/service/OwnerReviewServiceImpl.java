package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.NotReviewReplyOwnerException;
import com.sparta.dingdong.domain.review.exception.NotStoreOwnerException;
import com.sparta.dingdong.domain.review.exception.ReviewAlreadyRepliedException;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.exception.ReviewReplyNotFoundException;
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

	public ReviewReply findReviewReply(UUID replyId) {
		return reviewReplyRepository.findById(replyId).orElseThrow(ReviewReplyNotFoundException::new);
	}

	@Override
	@Transactional
	public void createReply(UUID reviewId, UserAuth userDetails, OwnerReviewDto.CreateReply request) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		// 답글이 이미 존재하는지 확인
		ReviewReply existingReviewReply = reviewReplyRepository.findByReview(review).orElse(null);

		if (existingReviewReply != null) {
			if (existingReviewReply.getDeletedBy() == null && existingReviewReply.getDeletedAt() == null) {
				throw new ReviewAlreadyRepliedException(reviewId);
			} else {
				existingReviewReply.reactivate(review, user, request);
				return;
			}
		}

		if (!review.getOrder().getStore().getOwner().equals(user)) {
			throw new NotStoreOwnerException("해당 리뷰 가게의 사장님만 답글을 작성할 수 있습니다.");
		}

		ReviewReply reply = ReviewReply.createReviewReply(review, user, request);

		reviewReplyRepository.save(reply);
	}

	@Override
	@Transactional
	public void updateReply(UUID reviewId, UUID replyId, UserAuth userDetails, OwnerReviewDto.UpdateReply request) {

		ReviewReply reply = findReviewReply(replyId);

		User user = userService.findByUser(userDetails);

		if (!reply.getOwner().equals(user)) {
			throw new NotReviewReplyOwnerException();
		}

		reply.updateReply(request);
	}

	@Override
	@Transactional
	public void deleteReply(UUID reviewId, UUID replyId, UserAuth userDetails) {

		ReviewReply reply = findReviewReply(replyId);

		User user = userService.findByUser(userDetails);

		if (!reply.getOwner().equals(user)) {
			throw new NotReviewReplyOwnerException();
		}

		reply.deleteReply(user);
	}
}
