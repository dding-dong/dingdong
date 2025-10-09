package com.sparta.dingdong.domain.payment.exception;

public class PaymentNotFoundException extends RuntimeException {

	public PaymentNotFoundException(String massage) {
		super(massage);
	}

	public PaymentNotFoundException() {
		super("결제 정보가 없습니다.");
	}
}
