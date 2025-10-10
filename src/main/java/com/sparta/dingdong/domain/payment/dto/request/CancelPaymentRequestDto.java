package com.sparta.dingdong.domain.payment.dto.request;

import java.util.UUID;

import lombok.Getter;

@Getter
public class CancelPaymentRequestDto {

	private UUID orderId;

	private String refundReason;
}
