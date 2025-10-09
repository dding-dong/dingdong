package com.sparta.dingdong.domain.payment.dto.request;

import java.math.BigInteger;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequestDto {

	@NotNull
	private final UUID orderId;

	@NotNull
	private final BigInteger amount;
	
	private String successUrl = "/v1/payments/toss/success";

	private String failUrl = "/v1/payments/toss/fail";

	public PaymentRequestDto(UUID orderId, BigInteger amount, String successUrl, String failUrl) {
		this.orderId = orderId;
		this.amount = amount;
		this.successUrl = (successUrl != null) ? successUrl : "/v1/payments/toss/success";
		this.failUrl = (failUrl != null) ? failUrl : "/v1/payments/toss/fail";
	}
}
