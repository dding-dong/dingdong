package com.sparta.dingdong.domain.user.exception;

public class AddressNotMatchedException extends RuntimeException {

	public AddressNotMatchedException() {
		super("해당 유저의 주소가 아닙니다.");
	}

	public AddressNotMatchedException(String message) {
		super(message);
	}
}
