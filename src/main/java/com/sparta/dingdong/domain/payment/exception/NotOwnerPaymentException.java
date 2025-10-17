package com.sparta.dingdong.domain.payment.exception;

public class NotOwnerPaymentException extends RuntimeException {

	public NotOwnerPaymentException(String message) {
		super(message);
	}

}
