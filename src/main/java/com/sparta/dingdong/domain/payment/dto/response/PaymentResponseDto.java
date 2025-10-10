package com.sparta.dingdong.domain.payment.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.payment.dto.request.PaymentRequestDto;
import com.sparta.dingdong.domain.payment.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {

	private UUID paymentId;

	private UUID orderId;

	private BigInteger amount;

	private String successUrl;

	private String failUrl;

	public static PaymentResponseDto from(Payment savePayment, PaymentRequestDto request) {
		return PaymentResponseDto.builder()
			.paymentId(savePayment.getId())
			.orderId(savePayment.getOrder().getId())
			.amount(savePayment.getAmount())
			.successUrl(request.getSuccessUrl())
			.failUrl(request.getFailUrl())
			.build();
	}
}
