package com.sparta.dingdong.domain.review.exception;

import java.util.UUID;

public class OrderAlreadyReviewedException extends RuntimeException {

	public OrderAlreadyReviewedException() {
		super("이미 해당 주문에 대한 리뷰가 존재합니다.");
	}

	public OrderAlreadyReviewedException(UUID orderId) {
		super("이미 주문(" + orderId + ")에 대한 리뷰가 존재합니다.");
	}

	public OrderAlreadyReviewedException(String message) {
		super(message);
	}

}
