package com.sparta.dingdong.domain.order.exception;

public class OrderPermissionDeniedException extends RuntimeException {

	public OrderPermissionDeniedException(String message) {
		super(message);
	}

	public OrderPermissionDeniedException() {
		super("해당 주문에 대한 접근 권한이 없습니다.");
	}
}
