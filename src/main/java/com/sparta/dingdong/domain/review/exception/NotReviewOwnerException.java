package com.sparta.dingdong.domain.review.exception;

public class NotReviewOwnerException extends RuntimeException {

	public NotReviewOwnerException() {
		super("리뷰 작성자가 아닙니다.");
	}

	public NotReviewOwnerException(String message) {
		super(message);
	}
}
