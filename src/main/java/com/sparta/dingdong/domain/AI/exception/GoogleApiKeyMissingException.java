package com.sparta.dingdong.domain.AI.exception;

public class GoogleApiKeyMissingException extends RuntimeException {
	public GoogleApiKeyMissingException() {
		super("Google API 키가 설정되어 있지 않습니다.");
	}

	public GoogleApiKeyMissingException(String message) {
		super(message);
	}
}
