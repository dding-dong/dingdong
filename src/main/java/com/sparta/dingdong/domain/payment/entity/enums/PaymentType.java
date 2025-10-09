package com.sparta.dingdong.domain.payment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

	NORMAL("NORMAL"),
	BRANDPAY("BRANDPAY");

	private final String value;
}
