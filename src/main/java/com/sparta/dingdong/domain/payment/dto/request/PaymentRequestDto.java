package com.sparta.dingdong.domain.payment.dto.request;

import java.math.BigInteger;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequestDto {

	@NotNull
	@Schema(description = "주문 아이디")
	private final UUID orderId;

	@NotNull
	@Schema(description = "결제 가격", example = "50000")
	private final BigInteger amount;

	@Schema(description = "결제 성공 url", example = "/v1/payments/toss/success")
	private String successUrl = "/v1/payments/toss/success";

	@Schema(description = "결제 실패 url", example = "/v1/payments/toss/fail")
	private String failUrl = "/v1/payments/toss/fail";

	public PaymentRequestDto(UUID orderId, BigInteger amount, String successUrl, String failUrl) {
		this.orderId = orderId;
		this.amount = amount;
		this.successUrl = (successUrl != null) ? successUrl : "/v1/payments/toss/success";
		this.failUrl = (failUrl != null) ? failUrl : "/v1/payments/toss/fail";
	}
}
