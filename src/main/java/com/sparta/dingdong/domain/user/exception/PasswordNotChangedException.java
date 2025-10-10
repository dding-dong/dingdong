package com.sparta.dingdong.domain.user.exception;

public class PasswordNotChangedException extends RuntimeException {

	public PasswordNotChangedException() {
		super("현재 계정의 비밀번호와 같습니다.");
	}

	public PasswordNotChangedException(String message) {
		super(message);
	}
}
