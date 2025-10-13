package com.sparta.dingdong.domain.store.exception;

public class NotStoreOwnerException extends RuntimeException {
	public NotStoreOwnerException() {
		super("본인 가게의 배달지역만 삭제 가능합니다.");
	}

	public NotStoreOwnerException(String message) {
		super(message);
	}
}
