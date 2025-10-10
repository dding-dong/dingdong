package com.sparta.dingdong.domain.user.exception;

public class InvalidPasswordException extends RuntimeException {

	public InvalidPasswordException() {
		super("유효하지 않은 비밀번호입니다.");
	}

	public InvalidPasswordException(String message) {
		super(message);
	}
}
