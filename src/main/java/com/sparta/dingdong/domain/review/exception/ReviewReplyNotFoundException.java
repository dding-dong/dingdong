package com.sparta.dingdong.domain.review.exception;

public class ReviewReplyNotFoundException extends RuntimeException {

	public ReviewReplyNotFoundException() {
		super("해당 리뷰의 댓글을 찾을 수 없습니다.");
	}

	public ReviewReplyNotFoundException(String message) {
		super(message);
	}
}
