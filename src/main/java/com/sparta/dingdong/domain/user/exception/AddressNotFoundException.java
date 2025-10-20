package com.sparta.dingdong.domain.user.exception;

public class AddressNotFoundException extends RuntimeException {

	public AddressNotFoundException() {
		super("주소가 존재하지 않습니다.");
	}

	public AddressNotFoundException(String message) {
		super(message);
	}
}
