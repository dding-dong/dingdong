package com.sparta.dingdong.domain.order.exception;

public class OrderDeliveryUnavailableException extends RuntimeException {

    public OrderDeliveryUnavailableException(String message) {
        super(message);
    }

    public OrderDeliveryUnavailableException() {
        super("해당 지역은 배달 불가지역입니다.");
    }
}

