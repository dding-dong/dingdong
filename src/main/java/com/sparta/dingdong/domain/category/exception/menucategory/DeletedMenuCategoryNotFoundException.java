package com.sparta.dingdong.domain.category.exception.menucategory;

public class DeletedMenuCategoryNotFoundException extends RuntimeException {
	public DeletedMenuCategoryNotFoundException() {
		super("삭제된 메뉴 카테고리를 찾을 수 없습니다.");
	}

	public DeletedMenuCategoryNotFoundException(String message) {
		super(message);
	}
}
