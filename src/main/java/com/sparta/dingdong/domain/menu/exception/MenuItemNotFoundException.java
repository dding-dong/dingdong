package com.sparta.dingdong.domain.menu.exception;

public class MenuItemNotFoundException extends RuntimeException {
	public MenuItemNotFoundException() {
		super("메뉴 아이템을 찾을 수 없습니다.");
	}

	public MenuItemNotFoundException(String message) {
		super(message);
	}
}
