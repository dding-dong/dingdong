package com.sparta.dingdong.domain.review.exception;

public class NotStoreOwnerException extends RuntimeException {

	public NotStoreOwnerException(String message) {
		super(message);
	}

	public NotStoreOwnerException() {
		super("리뷰 답글 작성은 가게 사장님만 가능합니다.");
	}
}
