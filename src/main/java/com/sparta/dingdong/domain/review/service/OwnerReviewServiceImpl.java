package com.sparta.dingdong.domain.review.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.request.OwnerCreateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.request.OwnerUpdateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewReplyDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewReplyResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerStoreReviewsDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.review.exception.NotReviewReplyOwnerException;
import com.sparta.dingdong.domain.review.exception.NotStoreOwnerException;
import com.sparta.dingdong.domain.review.exception.ReviewAlreadyRepliedException;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;
import com.sparta.dingdong.domain.review.exception.ReviewReplyNotFoundException;
import com.sparta.dingdong.domain.review.repository.ReviewQueryRepository;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.review.repository.vo.OwnerReviewWithReplyVo;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.service.StoreService;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerReviewServiceImpl implements OwnerReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final UserService userService;
	private final ReviewQueryRepository reviewQueryRepository;
	private final StoreService storeService;

	public Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
	}

	public ReviewReply findReviewReply(UUID replyId) {
		return reviewReplyRepository.findById(replyId).orElseThrow(ReviewReplyNotFoundException::new);
	}

	private void validateStoreOwner(Review review, User user) {
		if (!review.getOrder().getStore().getOwner().equals(user)) {
			throw new NotStoreOwnerException("해당 리뷰 가게의 사장님만 답글을 작성할 수 있습니다.");
		}
	}

	/**
	 * 기존 답글이 있으면 처리한다.
	 * - 삭제되지 않은 답글이 있으면 예외 발생
	 * - 삭제된 답글이면 재활성화 후 true 반환
	 */
	private boolean handleExistingReply(Review review, User user, OwnerCreateReplyRequestDto request) {
		ReviewReply existingReply = review.getReviewReply();

		if (existingReply == null) {
			return false; // 답글 없음 → 신규 생성 진행
		}

		if (existingReply.getDeletedBy() == null && existingReply.getDeletedAt() == null) {
			throw new ReviewAlreadyRepliedException(review.getId());
		}

		// 삭제된 답글이라면 재활성화
		existingReply.reactivate(review, user, request);
		return true;
	}

	@Override
	@Transactional
	public void createReply(UUID reviewId, UserAuth userDetails, OwnerCreateReplyRequestDto request) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		validateStoreOwner(review, user);

		// 기존 답글 처리
		if (handleExistingReply(review, user, request)) {
			return; // 재활성화 후 종료
		}

		// 신규 답글 생성
		ReviewReply reply = ReviewReply.createReviewReply(review, user, request);
		reviewReplyRepository.save(reply);
	}

	@Override
	@Transactional
	public void updateReply(UUID reviewId, UUID replyId, UserAuth userDetails, OwnerUpdateReplyRequestDto request) {

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

	public ReviewReply findActiveReviewReply(Review review) {
		return reviewReplyRepository.findByReviewAndDeletedAtIsNullAndDeletedByIsNull(review).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public OwnerReviewDetailsResponseDto getReviewDetails(UUID reviewId, UserAuth userDetails) {
		Review review = findReview(reviewId);

		User user = userService.findByUser(userDetails);

		// 해당 리뷰가 내 가게 리뷰인지 체크
		if (!review.getStore().getOwner().equals(user)) {
			throw new NotStoreOwnerException("본인 가게의 리뷰만 조회할 수 있습니다.");
		}

		if (!review.isActive()) {
			throw new ReviewNotFoundException();
		}

		ReviewReply reviewReply = findActiveReviewReply(review);

		OwnerReviewReplyDetailsResponseDto replyDetails = null;
		if (reviewReply != null) {
			replyDetails = OwnerReviewReplyDetailsResponseDto.builder()
				.replyId(reviewReply.getId())
				.ownerId(reviewReply.getOwner().getId())
				.content(reviewReply.getContent())
				.isDisplayed(reviewReply.isDisplayed())
				.build();
		}

		OwnerReviewDetailsResponseDto reviewDetails = OwnerReviewDetailsResponseDto.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.orderId(review.getOrder().getId())
			.storeId(review.getStore().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.imageUrl1(review.getImageUrl1())
			.imageUrl2(review.getImageUrl2())
			.imageUrl3(review.getImageUrl3())
			.reply(replyDetails)
			.isDisplayed(review.isDisplayed())
			.build();

		return reviewDetails;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OwnerStoreReviewsDto> getOwnerReviews(UserAuth userDetails) {
		Long ownerId = userService.findByUser(userDetails).getId();

		List<OwnerReviewWithReplyVo> voList = reviewQueryRepository.findAllActiveReviewsWithReplyByOwner(ownerId);

		// StoreId 기준으로 그룹핑
		Map<UUID, OwnerStoreReviewsDto> grouped = new LinkedHashMap<>();

		for (OwnerReviewWithReplyVo vo : voList) {
			OwnerStoreReviewsDto storeDto = grouped.computeIfAbsent(
				vo.getStoreId(),
				id -> OwnerStoreReviewsDto.builder()
					.storeId(vo.getStoreId())
					.storeName(vo.getStoreName())
					.reviews(new ArrayList<>())
					.build()
			);

			OwnerReviewReplyResponseDto replyDto = null;
			if (vo.getReplyId() != null) {
				replyDto = OwnerReviewReplyResponseDto.builder()
					.replyId(vo.getReplyId())
					.ownerId(vo.getOwnerId())
					.content(vo.getReplyContent())
					.isDisplayed(vo.getIsReviewReplyDisplayed())
					.build();
			}

			OwnerReviewResponseDto reviewDto = OwnerReviewResponseDto.builder()
				.reviewId(vo.getReviewId())
				.userId(vo.getUserId())
				.rating(vo.getRating())
				.content(vo.getContent())
				.imageUrl1(vo.getImageUrl1())
				.imageUrl2(vo.getImageUrl2())
				.imageUrl3(vo.getImageUrl3())
				.reply(replyDto)
				.isDisplayed(vo.getIsReviewDisplayed())
				.build();

			storeDto.getReviews().add(reviewDto);
		}

		return new ArrayList<>(grouped.values());
	}
	
	@Override
	@Transactional(readOnly = true)
	public OwnerStoreReviewsDto getOwnerStoreReviews(UserAuth userDetails, UUID storeId) {
		Long ownerId = userService.findByUser(userDetails).getId();

		Store store = storeService.getStoreOrThrow(storeId);

		List<OwnerReviewWithReplyVo> voList = reviewQueryRepository.findAllActiveReviewsWithReplyByStore(store.getId(),
			ownerId);

		List<OwnerReviewResponseDto> reviewDtos = voList.stream()
			.map(vo -> OwnerReviewResponseDto.builder()
				.reviewId(vo.getReviewId())
				.userId(vo.getUserId())
				.rating(vo.getRating())
				.content(vo.getContent())
				.imageUrl1(vo.getImageUrl1())
				.imageUrl2(vo.getImageUrl2())
				.imageUrl3(vo.getImageUrl3())
				.isDisplayed(vo.getIsReviewDisplayed())
				.reply(vo.getReplyId() != null ? OwnerReviewReplyResponseDto.builder()
					.replyId(vo.getReplyId())
					.ownerId(vo.getOwnerId())
					.content(vo.getReplyContent())
					.isDisplayed(vo.getIsReviewReplyDisplayed())
					.build()
					: null)
				.build())
			.toList();

		// 일단 voList 에서 storeId, storeName은 동일하므로 첫 번째 값 꺼내서 DTO 구성
		OwnerReviewWithReplyVo firstVo = voList.get(0);

		return OwnerStoreReviewsDto.builder()
			.storeId(firstVo.getStoreId())
			.storeName(firstVo.getStoreName())
			.reviews(reviewDtos)
			.build();
	}
}
