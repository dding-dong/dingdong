package com.sparta.dingdong.domain.order.exception;

public class InvalidOrderStatusException extends RuntimeException {

	public InvalidOrderStatusException(String message) {
		super(message);
	}

	public InvalidOrderStatusException() {
		super("잘못된 주문 상태입니다.");
	}
}
