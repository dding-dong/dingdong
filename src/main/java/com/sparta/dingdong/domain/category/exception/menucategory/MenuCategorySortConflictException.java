package com.sparta.dingdong.domain.category.exception.menucategory;

public class MenuCategorySortConflictException extends RuntimeException {
	public MenuCategorySortConflictException() {
		super("해당 정렬 순서를 가진 카테고리가 이미 존재합니다.");
	}

	public MenuCategorySortConflictException(String message) {
		super(message);
	}
}
