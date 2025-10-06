package com.sparta.dingdong.domain.review.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.review.service.ManagerReviewService;

import io.swagger.v3.oas.annotations.Operation;
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
	public ResponseEntity<BaseResponseDto<?>> hideReview(@PathVariable UUID reviewId) {
		managerReviewService.hideReview(reviewId);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰가 숨김처리 되었습니다."));
	}
}
