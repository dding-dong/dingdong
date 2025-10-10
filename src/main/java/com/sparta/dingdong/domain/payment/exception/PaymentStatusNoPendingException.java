package com.sparta.dingdong.domain.payment.exception;

public class PaymentStatusNoPendingException extends RuntimeException {
	public PaymentStatusNoPendingException(String message) {
		super(message);
	}

	public PaymentStatusNoPendingException() {
		super("결제 요청을 다시 해주세요.");
	}
}
