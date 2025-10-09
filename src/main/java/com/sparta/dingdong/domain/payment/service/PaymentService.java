package com.sparta.dingdong.domain.payment.service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentsRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.PaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentResponseDto;

public interface PaymentService {
	PaymentResponseDto createPayment(UserAuth userAuth, PaymentRequestDto request);

	void createConfirmPayments(UserAuth userAuth, ConfirmPaymentsRequestDto request);

	void refundPayment(Order order, String refundReason);
}
