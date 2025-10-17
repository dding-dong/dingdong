package com.sparta.dingdong.domain.order.exception;

public class OrderAlreadyCompletedException extends RuntimeException {

	public OrderAlreadyCompletedException(String message) {
		super(message);
	}

	public OrderAlreadyCompletedException() {
		super("이미 완료된 주문입니다.");
	}
}
