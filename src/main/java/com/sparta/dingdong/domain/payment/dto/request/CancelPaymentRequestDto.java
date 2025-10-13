package com.sparta.dingdong.domain.payment.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CancelPaymentRequestDto {

	@Schema(description = "주문 아이디")
	private UUID orderId;

	@Schema(description = "결제 취소 이유")
	private String refundReason;
}
