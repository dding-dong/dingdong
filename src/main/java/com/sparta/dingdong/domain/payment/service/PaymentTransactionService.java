package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.domain.payment.entity.Payment;

public interface PaymentTransactionService {

	void markPaymentFailed(Payment payment);
}
