package com.sparta.dingdong.domain.payment.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FakeTossPaymentFailResponseDto {
	private UUID orderId;
	private String errorCode;
	private String errorMessage;
}
