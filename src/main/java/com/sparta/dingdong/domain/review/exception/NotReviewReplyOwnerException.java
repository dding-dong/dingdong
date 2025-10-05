package com.sparta.dingdong.domain.review.exception;

public class NotReviewReplyOwnerException extends RuntimeException {

	public NotReviewReplyOwnerException() {
		super("리뷰 답글의 작성자가 아닙니다.");
	}

	public NotReviewReplyOwnerException(String message) {
		super(message);
	}
}
