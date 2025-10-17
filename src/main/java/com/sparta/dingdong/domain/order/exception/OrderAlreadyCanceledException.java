package com.sparta.dingdong.domain.order.exception;

public class OrderAlreadyCanceledException extends RuntimeException {

    public OrderAlreadyCanceledException(String message) {
        super(message);
    }

    public OrderAlreadyCanceledException() {
        super("이미 취소된 주문입니다.");
    }
}
