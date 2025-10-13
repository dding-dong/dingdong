package com.sparta.dingdong.domain.category.exception.storecategory;

public class StoreCategoryAlreadyActiveException extends RuntimeException {
	public StoreCategoryAlreadyActiveException() {
		super("이미 활성화된 가게 카테고리입니다.");
	}

	public StoreCategoryAlreadyActiveException(String message) {
		super(message);
	}
}
