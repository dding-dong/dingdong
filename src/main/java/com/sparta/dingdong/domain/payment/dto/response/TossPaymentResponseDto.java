package com.sparta.dingdong.domain.payment.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class TossPaymentResponseDto {

	private UUID orderId;
	private String status;
	private String paymentKey;
}
