package com.sparta.dingdong.domain.payment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.payment.dto.response.AdminPaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "결제 API", description = "결제 API 입니다.")
public class PaymentControllerV1 {

	private final PaymentService paymentService;

	@Operation(summary = "CUSTOMER 결제 상세 조회 API", description = "고객이 주문에 대해 결제 상세조회를 요청합니다.")
	@GetMapping("/orders/{orderId}/payments")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	ResponseEntity<BaseResponseDto<PaymentDetailResponseDto>> getPaymentDetails(
		@AuthenticationPrincipal UserAuth userAuth,
		@PathVariable UUID orderId
	) {
		PaymentDetailResponseDto paymentDetailResponseDto = paymentService.getPaymentDetails(userAuth, orderId);
		return ResponseEntity.ok(BaseResponseDto.success("결제 상세 조회입니다.", paymentDetailResponseDto));
	}

	@Operation(summary = "Manager&Master 결제 상세 조회 API", description = "매니저와 마스터가 사용자의 주문 결제 상세조회를 요청합니다.")
	@GetMapping("/admin/orders/{orderId}/payments")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MASTER')")
	ResponseEntity<BaseResponseDto<AdminPaymentDetailResponseDto>> getAdminPaymentDetails(
		@AuthenticationPrincipal UserAuth userAuth,
		@PathVariable UUID orderId
	) {
		AdminPaymentDetailResponseDto adminPaymentDetailResponseDto = paymentService.getAdminPaymentDetails(orderId);
		return ResponseEntity.ok(BaseResponseDto.success("결제 상세 조회입니다.", adminPaymentDetailResponseDto));
	}
}
