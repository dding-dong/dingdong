package com.sparta.dingdong.domain.category.exception.menucategory;

public class MenuCategoryNotFoundException extends RuntimeException {
	public MenuCategoryNotFoundException() {
		super("메뉴 카테고리를 찾을 수 없습니다.");
	}

	public MenuCategoryNotFoundException(String message) {
		super(message);
	}
}
