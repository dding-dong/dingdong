package com.sparta.dingdong.domain.order.exception;

public class OrderCancelTimeExceededException extends RuntimeException {
    public OrderCancelTimeExceededException(String message) {
        super(message);
    }
}
