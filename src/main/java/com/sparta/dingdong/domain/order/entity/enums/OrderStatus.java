package com.sparta.dingdong.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	PENDING("PENDING"), // 주문 요청 상태 값 입니다. 결제 요청 전
	REQUESTED("REQUESTED"),   // 결제 완료 후 생성(요청됨)
	CANCELED("CANCELED"),     // 고객 주문 취소
	REJECTED("REJECTED"),    // 사장님 주문 취소
	ACCEPTED("ACCEPTED"),    // 사장님 수락
	COOKING("COOKING"),     // 조리중
	READY("READY"),       // 조리완료(픽업 준비)
	DELIVERED("DELIVERED");   // 배달완료

	private final String value;
}
