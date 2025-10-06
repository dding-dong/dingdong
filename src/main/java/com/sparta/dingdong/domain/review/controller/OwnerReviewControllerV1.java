package com.sparta.dingdong.domain.review.controller;

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
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;
import com.sparta.dingdong.domain.review.service.OwnerReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
	public ResponseEntity<BaseResponseDto<?>> createReply(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails,
		@RequestBody OwnerReviewDto.CreateReply request) {

		ownerReviewService.createReply(reviewId, userDetails, request);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰에 댓글이 등록되었습니다."));
	}

	@Operation(summary = "리뷰 답글 수정 API", description = "가게 주인이 리뷰 답글을 수정합니다.")
	@PutMapping("/reviews/{reviewId}/reply/{replyId}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> updateReply(@PathVariable UUID reviewId,
		@PathVariable UUID replyId,
		@AuthenticationPrincipal UserAuth userDetails,
		@RequestBody OwnerReviewDto.UpdateReply request) {

		ownerReviewService.updateReply(reviewId, replyId, userDetails, request);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰에 댓글이 수정되었습니다."));
	}

	@Operation(summary = "리뷰 답글 삭제 API", description = "가게 주인이 리뷰 답글을 삭제합니다.")
	@DeleteMapping("/reviews/{reviewId}/reply/{replyId}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> deleteReply(@PathVariable UUID reviewId,
		@PathVariable UUID replyId,
		@AuthenticationPrincipal UserAuth userDetails) {

		ownerReviewService.deleteReply(reviewId, replyId, userDetails);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰에 댓글이 삭제되었습니다."));
	}

	@Operation(summary = "가게 주인 리뷰 상세 조회 API", description = "가게 주인이 리뷰를 상세 조회합니다.")
	@GetMapping("/owners/reviews/{reviewId}")
	public ResponseEntity<BaseResponseDto<?>> getOwnerReviewDetails(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails) {
		OwnerReviewDto.ReviewDetails details = ownerReviewService.getReviewDetails(reviewId, userDetails);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰 상세 조회입니다.", details));
	}
}
