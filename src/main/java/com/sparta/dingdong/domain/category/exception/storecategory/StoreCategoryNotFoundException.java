package com.sparta.dingdong.domain.category.exception.storecategory;

public class StoreCategoryNotFoundException extends RuntimeException {
	public StoreCategoryNotFoundException() {
		super("가게 카테고리를 찾을 수 없습니다.");
	}

	public StoreCategoryNotFoundException(String message) {
		super(message);
	}
}
