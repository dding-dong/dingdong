package com.sparta.dingdong.domain.review.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.review.dto.ManagerReviewDto;
import com.sparta.dingdong.domain.review.service.ManagerReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "매니저 리뷰 API", description = "매니저 리뷰 API 입니다.")
public class ManagerReviewControllerV1 {

	private final ManagerReviewService managerReviewService;

	@Operation(summary = "매니저 리뷰 숨김 API", description = "매니저가 리뷰를 숨김 처리합니다.")
	@PatchMapping("/admin/reviews/{reviewId}")
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public ResponseEntity<BaseResponseDto<?>> hideReview(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId) {
		managerReviewService.hideReview(reviewId);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 숨김처리 되었습니다."));
	}

	@Operation(summary = "매니저 리뷰 상세 조회 API", description = "매니저가 리뷰를 상세 조회할 수 있습니다.")
	@GetMapping("/admin/reviews/{reviewId}")
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public ResponseEntity<BaseResponseDto<?>> getReviewDetails(
		@Parameter(description = "리뷰 UUID") @PathVariable UUID reviewId) {
		ManagerReviewDto.Review review = managerReviewService.getReviewDetails(reviewId);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 숨김처리 되었습니다.", review));
	}

	@Operation(summary = "매니저 리뷰 목록 조회 API", description = "매니저가 리뷰 목록을 조회할 수 있습니다.")
	@GetMapping("/admin/reviews")
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public ResponseEntity<BaseResponseDto<?>> getReviews() {
		List<ManagerReviewDto.Review> list = managerReviewService.getReviews();
		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 숨김처리 되었습니다.", list));
	}
}
