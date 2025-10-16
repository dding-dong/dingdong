package com.sparta.dingdong.domain.order.exception;

import java.math.BigInteger;

public class OrderBelowMinPriceException extends RuntimeException {

    public OrderBelowMinPriceException(BigInteger totalPrice, BigInteger minOrderPrice) {
        super(String.format(
                "주문 금액이 매장의 최소 주문 금액(%s원)보다 적습니다. 현재 주문 금액: %s원",
                minOrderPrice, totalPrice
        ));
    }

    public OrderBelowMinPriceException() {
        super("주문 금액이 매장의 최소 주문 금액보다 적습니다.");
    }
}
