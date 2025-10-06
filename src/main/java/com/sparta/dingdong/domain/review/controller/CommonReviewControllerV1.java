package com.sparta.dingdong.domain.review.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.review.dto.CommonReviewDto;
import com.sparta.dingdong.domain.review.service.CommonReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "공통 리뷰 API", description = "공통 리뷰 API 입니다.")
public class CommonReviewControllerV1 {

	private final CommonReviewService commonReviewService;

	@Operation(summary = "공통 리뷰 상세 조회 API", description = "고객과 오너가 리뷰 상세 조회합니다.")
	@GetMapping("/reviews/{reviewId}")
	@PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> selectReview(@PathVariable UUID reviewId) {
		CommonReviewDto.ReviewDetails details = commonReviewService.selectReview(reviewId);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰를 상세 조회합니다.", details));
	}

	@Operation(summary = "공통 리뷰 전체 조회 API", description = "고객과 오너가 리뷰 전체 조회합니다.")
	@GetMapping("/reviews")
	@PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> selectReviews() {
		List<CommonReviewDto.Review> list = commonReviewService.selectReviews();
		return ResponseEntity.ok(BaseResponseDto.success("리뷰를 상세 조회합니다.", list));
	}
}
