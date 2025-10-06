package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.NotReviewOwnerException;
import com.sparta.dingdong.domain.review.exception.OrderAlreadyReviewedException;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewQueryRepository;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.review.repository.vo.ReviewWithReplyVo;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReviewServiceImpl implements CustomerReviewService {

	private final UserService userService;
	private final OrderService orderService;
	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final ReviewQueryRepository reviewQueryRepository;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	public ReviewReply findActiveReviewReply(Review review) {
		return reviewReplyRepository.findByReviewAndDeletedAtIsNullAndDeletedByIsNull(review).orElse(null);
	}

	/**
	 * 기존 리뷰가 존재하면 처리
	 * 활성화된 리뷰 -> 예외 발생
	 * 삭제/숨김된 리뷰 -> 재활성화 후 true 반환
	 * 존재하지 않으면 false 반환
	 */
	private boolean handleExistingReview(Review existingReview, User user, CustomerReviewDto.CreateReview request,
		UUID orderId) {

		if (existingReview == null) {
			return false;
		}
		if (existingReview.isActive()) {
			throw new OrderAlreadyReviewedException(orderId);
		}

		existingReview.reactivate(user, request);
		return true;
	}

	@Override
	@Transactional
	public void createReview(UUID orderId, UserAuth userDetails, CustomerReviewDto.CreateReview request) {
		User user = userService.findByUser(userDetails);
		Order order = orderService.findByOrder(orderId);

		Review existingReview = reviewRepository.findByOrder(order).orElse(null);

		if (handleExistingReview(existingReview, user, request, orderId)) {
			return; // 기존 리뷰가 재활성화되었다면 메서드 종료
		}

		Review review = Review.create(user, order, request);
		reviewRepository.save(review);
	}

	@Override
	@Transactional
	public void updateReview(UUID reviewId, UserAuth userDetails, CustomerReviewDto.UpdateReview request) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		if (!review.getUser().getId().equals(user.getId())) {
			throw new NotReviewOwnerException();
		}

		review.updateReview(request);
	}

	@Override
	@Transactional
	public void deleteReview(UUID reviewId, UserAuth userDetails) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		if (!review.getUser().getId().equals(user.getId())) {
			throw new NotReviewOwnerException();
		}

		review.deleteReview(user);
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerReviewDto.ReviewDetails selectReviewDetails(UUID reviewId, UserAuth userDetails) {
		User user = userService.findByUser(userDetails);
		Review review = findReview(reviewId);

		// 내가 작성한 리뷰만 상세 조회할 수 있다.
		if (!review.getUser().getId().equals(user.getId())) {
			throw new NotReviewOwnerException("본인의 리뷰만 조회할 수 있습니다.");
		}

		// 고객은 삭제된 리뷰는 볼 수 없다.
		if (review.getDeletedAt() != null && review.getDeletedBy() != null) {
			throw new ReviewNotFoundException();
		}

		ReviewReply reviewReply = findActiveReviewReply(review);
		CustomerReviewDto.ReviewReplyDetails reviewReplyDto = null;

		// 리뷰 답글 노출 여부
		if (reviewReply != null && reviewReply.isDisplayed()) {
			reviewReplyDto = CustomerReviewDto.ReviewReplyDetails.builder()
				.replyId(reviewReply.getId())
				.ownerId(reviewReply.getOwner().getId())
				.content(reviewReply.getContent())
				.isDisplayed(reviewReply.isDisplayed())
				.build();
		}

		CustomerReviewDto.ReviewDetails reviewDetailsDto = CustomerReviewDto.ReviewDetails.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.orderId(review.getOrder().getId())
			.storeId(review.getStore().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.imageUrl1(review.getImageUrl1())
			.imageUrl2(review.getImageUrl2())
			.imageUrl3(review.getImageUrl3())
			.reply(reviewReplyDto)
			.isDisplayed(review.isDisplayed())
			.build();

		return reviewDetailsDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerReviewDto.Review> selectActiveReviews(UserAuth userDetails) {
		User user = userService.findByUser(userDetails);
		List<ReviewWithReplyVo> voList = reviewQueryRepository.findAllActiveReviewsWithReplyByUser(user.getId());

		return voList.stream().map(vo -> {
			// 답글이 없으면 null 처리
			CustomerReviewDto.ReviewReply replyDto = null;
			if (vo.getReplyId() != null && vo.getReplyContent() != null) {
				replyDto = CustomerReviewDto.ReviewReply.builder()
					.replyId(vo.getReplyId())
					.ownerId(vo.getOwnerId())
					.content(vo.getReplyContent())
					.isDisplayed(vo.getIsReviewReplyDisplayed())
					.build();
			}

			return CustomerReviewDto.Review.builder()
				.reviewId(vo.getReviewId())
				.userId(vo.getUserId())
				.orderId(vo.getOrderId())
				.storeId(vo.getStoreId())
				.rating(vo.getRating())
				.content(vo.getContent())
				.imageUrl1(vo.getImageUrl1())
				.imageUrl2(vo.getImageUrl2())
				.imageUrl3(vo.getImageUrl3())
				.reply(replyDto)
				.isDisplayed(vo.getIsReviewDisplayed())
				.build();
		}).toList();
	}
}
