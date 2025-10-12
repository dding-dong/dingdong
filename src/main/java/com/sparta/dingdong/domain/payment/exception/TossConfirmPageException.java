package com.sparta.dingdong.domain.payment.exception;

public class TossConfirmPageException extends RuntimeException {

	private final String errorCode;  // Toss가 내려주는 에러 코드 (ex: PAYMENT_FAILED)
	private final String errorMessage; // Toss가 내려주는 에러 메시지

	public TossConfirmPageException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
