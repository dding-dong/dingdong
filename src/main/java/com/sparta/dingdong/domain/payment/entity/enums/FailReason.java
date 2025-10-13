package com.sparta.dingdong.domain.payment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FailReason {

	PAY_PROCESS_CANCELED("PAY_PROCESS_CANCELED"),
	PAY_PROCESS_ABORTED("PAY_PROCESS_ABORTED"),
	REJECT_CARD_COMPANY("REJECT_CARD_COMPANY");

	private final String value;
}
