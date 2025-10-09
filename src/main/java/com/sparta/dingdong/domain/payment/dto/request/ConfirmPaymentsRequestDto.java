package com.sparta.dingdong.domain.payment.dto.request;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ConfirmPaymentsRequestDto {

	@Schema(description = "주문 아이디")
	private UUID orderId;

	@Schema(description = "결제 가격", example = "50000")
	private BigInteger amount;

	@Schema(description = "결제 키(토스에서 주는 값)", example = "payment_abcdefg")
	private String paymentKey;

	@Schema(description = "결제 타입", example = "NORMAL")
	private PaymentType paymentType;
}
