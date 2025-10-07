package com.sparta.dingdong.domain.menu.exception;

public class MenuItemSoldOutException extends RuntimeException {
	public MenuItemSoldOutException() {
		super("해당 메뉴는 품절입니다.");
	}

	public MenuItemSoldOutException(String message) {
		super(message);
	}
}
