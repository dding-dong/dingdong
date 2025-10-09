package com.sparta.dingdong.domain.payment.exception;

public class PaymentAlreadyExistsException extends RuntimeException {

	public PaymentAlreadyExistsException(String message) {
		super(message);
	}

	public PaymentAlreadyExistsException() {
		super("이미 결제 진행 중인 주문입니다.");
	}
}
