package com.sparta.dingdong.domain.store.exception;

public class StoreNotFoundException extends RuntimeException {
	public StoreNotFoundException() {
		super("가게를 찾을 수 없습니다.");
	}

	public StoreNotFoundException(String message) {
		super(message);
	}
}
