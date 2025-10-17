package com.sparta.dingdong.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	PENDING("PENDING"),
	REQUESTED("REQUESTED"),
	CANCELED("CANCELED"),
	REJECTED("REJECTED"),
	ACCEPTED("ACCEPTED"),
	COOKING("COOKING"),
	READY("READY"),
	DELIVERED("DELIVERED");

	private final String value;
}
