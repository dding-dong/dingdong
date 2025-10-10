package com.sparta.dingdong.domain.user.exception;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException() {
		super("중복된 이메일입니다.");
	}

	public DuplicateEmailException(String message) {
		super(message);
	}
}
