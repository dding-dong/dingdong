package com.sparta.dingdong.domain.review.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.request.OwnerCreateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.request.OwnerUpdateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerStoreReviewsDto;
import com.sparta.dingdong.domain.review.service.OwnerReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "가게 주인 리뷰 API", description = "가게 주인 리뷰 API 입니다.")
public class OwnerReviewControllerV1 {

	private final OwnerReviewService ownerReviewService;

	@Operation(summary = "리뷰 답글 생성 API", description = "가게 주인이 리뷰 답글을 작성합니다.")
	@PostMapping("/reviews/{reviewId}/reply")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> createReply(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails,
		@Valid @RequestBody OwnerCreateReplyRequestDto request) {

		ownerReviewService.createReply(reviewId, userDetails, request);
		return ResponseEntity.ok(BaseResponseDto.success("오너 리뷰에 댓글이 등록되었습니다."));
	}

	@Operation(summary = "리뷰 답글 수정 API", description = "가게 주인이 리뷰 답글을 수정합니다.")
	@PutMapping("/reviews/{reviewId}/reply/{replyId}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> updateReply(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId,
		@Parameter(description = "리뷰 답글 UUID") @PathVariable UUID replyId,
		@AuthenticationPrincipal UserAuth userDetails,
		@Valid @RequestBody OwnerUpdateReplyRequestDto request) {

		ownerReviewService.updateReply(reviewId, replyId, userDetails, request);
		return ResponseEntity.ok(BaseResponseDto.success("오너 리뷰에 댓글이 수정되었습니다."));
	}

	@Operation(summary = "리뷰 답글 삭제 API", description = "가게 주인이 리뷰 답글을 삭제합니다.")
	@DeleteMapping("/reviews/{reviewId}/reply/{replyId}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> deleteReply(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId,
		@Parameter(description = "리뷰 답글 UUID") @PathVariable UUID replyId,
		@AuthenticationPrincipal UserAuth userDetails) {

		ownerReviewService.deleteReply(reviewId, replyId, userDetails);
		return ResponseEntity.ok(BaseResponseDto.success("오너 리뷰에 댓글이 삭제되었습니다."));
	}

	@Operation(summary = "가게 주인 리뷰 상세 조회 API", description = "가게 주인이 리뷰를 상세 조회합니다.")
	@GetMapping("/owners/reviews/{reviewId}")
	public ResponseEntity<BaseResponseDto<?>> getOwnerReviewDetails(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails) {
		OwnerReviewDetailsResponseDto details = ownerReviewService.getReviewDetails(reviewId, userDetails);
		return ResponseEntity.ok(BaseResponseDto.success("오너 리뷰 상세 조회입니다.", details));
	}

	@Operation(summary = "가게 주인 리뷰 전체 조회 API", description = "가게 주인이 리뷰를 전체 조회합니다.")
	@GetMapping("/owners/reviews")
	public ResponseEntity<BaseResponseDto<?>> getOwnerReviews(@AuthenticationPrincipal UserAuth userDetails) {
		List<OwnerStoreReviewsDto> reviews = ownerReviewService.getOwnerReviews(userDetails);
		return ResponseEntity.ok(BaseResponseDto.success("오너 리뷰 전체 조회입니다.", reviews));
	}

	@Operation(summary = "가게 리뷰 조회 API", description = "가게 주인이 가게의 리뷰를 조회합니다.")
	@GetMapping("/owners/stores/{storeId}/reviews")
	public ResponseEntity<BaseResponseDto<?>> getOwnerStoreReviews(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@AuthenticationPrincipal UserAuth userDetails) {
		OwnerStoreReviewsDto reviews = ownerReviewService.getOwnerStoreReviews(userDetails, storeId);
		return ResponseEntity.ok(BaseResponseDto.success("오너 가게 리뷰 조회입니다.", reviews));
	}
}
