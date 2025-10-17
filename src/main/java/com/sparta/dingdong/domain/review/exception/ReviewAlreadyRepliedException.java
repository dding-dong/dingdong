package com.sparta.dingdong.domain.review.exception;

import java.util.UUID;

public class ReviewAlreadyRepliedException extends RuntimeException {

	public ReviewAlreadyRepliedException() {
		super("이미 해당 리뷰에 대한 답글이 존재합니다.");
	}

	public ReviewAlreadyRepliedException(UUID reviewId) {
		super("이미 해당 리뷰(" + reviewId + ")에 대한 답글이 존재합니다.");
	}

	public ReviewAlreadyRepliedException(String message) {
		super(message);
	}
}
