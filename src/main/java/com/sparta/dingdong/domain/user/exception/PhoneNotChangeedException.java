package com.sparta.dingdong.domain.user.exception;

public class PhoneNotChangeedException extends RuntimeException {

	public PhoneNotChangeedException() {
		super("현재 계정의 전화번호가 같습니다.");
	}

	public PhoneNotChangeedException(String message) {
		super(message);
	}
}
