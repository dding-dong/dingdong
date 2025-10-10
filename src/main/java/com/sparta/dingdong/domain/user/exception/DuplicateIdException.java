package com.sparta.dingdong.domain.user.exception;

public class DuplicateIdException extends RuntimeException {

	public DuplicateIdException() {
		super("중복된 아이디입니다.");
	}

	public DuplicateIdException(String message) {
		super(message);
	}
}
