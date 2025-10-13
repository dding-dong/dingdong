package com.sparta.dingdong.domain.store.exception;

public class DeliveryAreaNotFoundException extends RuntimeException {
	public DeliveryAreaNotFoundException() {
		super("배달지역을 찾을 수 없습니다.");
	}

	public DeliveryAreaNotFoundException(String message) {
		super(message);
	}
}
