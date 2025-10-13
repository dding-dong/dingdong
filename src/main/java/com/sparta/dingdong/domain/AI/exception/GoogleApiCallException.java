package com.sparta.dingdong.domain.AI.exception;

public class GoogleApiCallException extends RuntimeException {
	public GoogleApiCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public GoogleApiCallException(String message) {
		super(message);
	}
}
