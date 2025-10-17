package com.sparta.dingdong.domain.payment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

	PENDING("PENDING"),
	PAID("PAID"),
	FAILED("FAILED"),
	REFUNDED("REFUNDED");

	private final String value;
}