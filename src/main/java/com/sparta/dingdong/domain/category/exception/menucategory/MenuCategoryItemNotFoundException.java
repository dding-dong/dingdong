package com.sparta.dingdong.domain.category.exception.menucategory;

public class MenuCategoryItemNotFoundException extends RuntimeException {
	public MenuCategoryItemNotFoundException() {
		super("해당 메뉴 카테고리의 메뉴 아이템이 존재하지 않습니다.");
	}

	public MenuCategoryItemNotFoundException(String message) {
		super(message);
	}
}
