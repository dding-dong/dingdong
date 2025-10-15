package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.entity.Payment;

public interface PaymentTransactionService {

	void markPaymentFailed(Payment payment);

	void refundPayment(Order order, String refundReason);
}
