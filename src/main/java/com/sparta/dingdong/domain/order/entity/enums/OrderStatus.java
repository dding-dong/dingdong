package com.sparta.dingdong.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	REQUESTED("REQUESTED"),   // 결제 완료 후 생성(요청됨)
	ACCEPTED("ACCEPTED"),    // 사장님 수락
	COOKING("COOKING"),     // 조리중
	READY("READY"),       // 조리완료(픽업 준비)
	DELIVERED("DELIVERED"),   // 배달완료
	CANCELED("CANCELED");     // 취소

	private final String value;
}
