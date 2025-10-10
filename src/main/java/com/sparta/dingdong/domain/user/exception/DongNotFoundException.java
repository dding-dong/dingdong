package com.sparta.dingdong.domain.user.exception;

public class DongNotFoundException extends RuntimeException {

	public DongNotFoundException() {
		super("동이 존재하지 않습니다.");
	}

	public DongNotFoundException(String message) {
		super(message);
	}
}
