package com.sparta.dingdong.domain.payment.exception;

public class PaymentAmountMismatchException extends RuntimeException {

	public PaymentAmountMismatchException(String message) {
		super(message);
	}

	public PaymentAmountMismatchException() {
		super("결제 가격이랑 요청주신 가격이 다릅니다.");
	}
}
