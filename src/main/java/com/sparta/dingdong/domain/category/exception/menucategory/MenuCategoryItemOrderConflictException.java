package com.sparta.dingdong.domain.category.exception.menucategory;

public class MenuCategoryItemOrderConflictException extends RuntimeException {
	public MenuCategoryItemOrderConflictException() {
		super("해당 카테고리에서 이미 같은 순서(orderNo)의 메뉴 아이템이 존재합니다.");
	}

	public MenuCategoryItemOrderConflictException(String message) {
		super(message);
	}
}
