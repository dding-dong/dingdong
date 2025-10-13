package com.sparta.dingdong.domain.payment.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

	NORMAL("NORMAL"),
	BRANDPAY("BRANDPAY");

	private final String value;

	@JsonCreator
	public static PaymentType from(String value) {
		for (PaymentType type : values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown payment type: " + value);
	}
}
