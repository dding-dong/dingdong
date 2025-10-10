package com.sparta.dingdong.domain.review.exception;

public class ReviewNotFoundException extends RuntimeException {

	public ReviewNotFoundException() {
		super("해당 리뷰를 찾을 수 없습니다.");
	}

	public ReviewNotFoundException(String message) {
		super(message);
	}
}
