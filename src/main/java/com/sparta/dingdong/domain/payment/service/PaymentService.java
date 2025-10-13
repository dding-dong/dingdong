package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.dto.response.AdminPaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentDetailResponseDto;

public interface PaymentService {

	void refundPayment(Order order, String refundReason);

	PaymentDetailResponseDto getPaymentDetails(UserAuth userAuth, UUID orderId);

	AdminPaymentDetailResponseDto getAdminPaymentDetails(UUID orderId);
}
