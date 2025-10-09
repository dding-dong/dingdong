package com.sparta.dingdong.domain.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.payment.dto.request.PaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.dingdong.domain.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "결제 API", description = "결제 API 입니다.")
public class PaymentControllerV1 {

	private final PaymentService paymentService;

	@Operation(summary = "결제 요청 API", description = "고객이 결제를 요청합니다.")
	@PostMapping
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	ResponseEntity<BaseResponseDto<?>> createPayment(@AuthenticationPrincipal UserAuth userAuth,
		@Validated @RequestBody PaymentRequestDto request) {
		PaymentResponseDto response = paymentService.createPayment(userAuth, request);
		return ResponseEntity.ok(BaseResponseDto.success("결제를 요청합니다", response));
	}
}
