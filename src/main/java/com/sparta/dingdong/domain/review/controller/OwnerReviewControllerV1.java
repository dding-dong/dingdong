package com.sparta.dingdong.domain.review.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;
import com.sparta.dingdong.domain.review.service.OwnerReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OwnerReviewControllerV1 {

	private final OwnerReviewService ownerReviewService;

	@PostMapping("/reviews/{reviewId}/reply")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BaseResponseDto<?>> createReply(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserAuth userDetails,
		@RequestBody OwnerReviewDto.CreateReply request) {

		ownerReviewService.createReply(reviewId, userDetails, request);
		return ResponseEntity.ok(BaseResponseDto.success("리뷰에 댓글 등록되었습니다."));
	}
}
