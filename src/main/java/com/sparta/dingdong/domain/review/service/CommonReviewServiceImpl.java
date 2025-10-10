package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.review.dto.response.CommonReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.CommonReviewReplyDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.CommonReviewReplyResponseDto;
import com.sparta.dingdong.domain.review.dto.response.CommonReviewResponseDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewQueryRepository;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.review.repository.vo.ReviewWithReplyActiveVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonReviewServiceImpl implements CommonReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final ReviewQueryRepository reviewQueryRepository;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	public ReviewReply findActiveReviewReply(Review review) {
		return reviewReplyRepository.findByReviewAndDeletedAtIsNullAndDeletedByIsNullAndIsDisplayedTrue(review)
			.orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public CommonReviewDetailsResponseDto selectReview(UUID reviewId) {
		Review review = findReview(reviewId);

		// 고객은 삭제 및 비활성화된 리뷰는 볼 수 없다.
		if (!review.isActive()) {
			throw new ReviewNotFoundException();
		}

		ReviewReply reviewReply = review.getReviewReply();
		CommonReviewReplyDetailsResponseDto reviewReplyDetails = null;

		// 달린 리뷰가 있고 활성화 되어있으면 DTO로 변환
		if (reviewReply != null && reviewReply.isActive()) {
			reviewReplyDetails = CommonReviewReplyDetailsResponseDto.builder()
				.replyId(reviewReply.getId())
				.ownerId(reviewReply.getOwner().getId())
				.content(reviewReply.getContent())
				.build();
		}

		return CommonReviewDetailsResponseDto.builder()
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
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommonReviewResponseDto> selectReviews() {
		List<ReviewWithReplyActiveVo> voList = reviewQueryRepository.findAllActiveReviewWithReply();
		return voList.stream()
			.map(vo -> {
				// ReviewReply DTO 생성 (답글이 있을 경우만)
				CommonReviewReplyResponseDto replyDto = null;
				if (vo.getReplyId() != null) {
					replyDto = CommonReviewReplyResponseDto.builder()
						.replyId(vo.getReplyId())
						.ownerId(vo.getOwnerId())
						.content(vo.getReplyContent())
						.build();
				}

				// Review DTO 생성
				return CommonReviewResponseDto.builder()
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
					.build();
			})
			.toList();
	}
}
