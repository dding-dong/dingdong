package com.sparta.dingdong.domain.review.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;
import com.sparta.dingdong.domain.review.service.CustomerReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "고객 리뷰 API", description = "고객 리뷰 API 입니다.")
public class CustomerReviewControllerV1 {

	private final CustomerReviewService customerReviewService;

	@Operation(summary = "고객 리뷰 등록 API", description = "고객이 주문에 대한 리뷰를 등록합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PostMapping("/orders/{orderId}/reviews")
	public ResponseEntity<BaseResponseDto<?>> createReview(@PathVariable UUID orderId,
		@AuthenticationPrincipal UserAuth userDetails,
		@RequestBody CustomerReviewDto.CreateReview request) {

		customerReviewService.createReview(orderId, userDetails, request);

		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 등록되었습니다."));
	}

	@Operation(summary = "리뷰 수정 API", description = "고객이 주문에 대한 리뷰를 수정합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PutMapping("/reviews/{reviewId}")
	public ResponseEntity<BaseResponseDto<?>> updateReview(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails,
		@RequestBody CustomerReviewDto.UpdateReview request) {

		customerReviewService.updateReview(reviewId, userDetails, request);

		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 수정되었습니다."));
	}

	@Operation(summary = "리뷰 삭제 API", description = "고객이 주문에 대한 리뷰를 삭제합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<BaseResponseDto<?>> deleteReview(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails) {

		customerReviewService.deleteReview(reviewId, userDetails);

		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 삭제되었습니다."));
	}
}
