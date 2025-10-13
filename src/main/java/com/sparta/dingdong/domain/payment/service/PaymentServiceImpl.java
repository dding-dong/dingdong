package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.response.AdminPaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.exception.PaymentNotFoundException;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderService orderService;

	private Payment findByPayment(Order order) {
		return paymentRepository.findByOrder(order).orElseThrow(PaymentNotFoundException::new);
	}

	@Transactional
	public void refundPayment(Order order, String refundReason) {
		// POST: /v1/payments/{paymentKey}/cancel 결제 취소 됐다고 생각하고
		// payment refundReason = client한테 받은 refundReason값, paymentStatus = "REFUNDED"
		Payment payment = findByPayment(order);

		payment.setRefundReason(refundReason);
		payment.changeStatus(PaymentStatus.REFUNDED);
	}

	@Override
	public PaymentDetailResponseDto getPaymentDetails(UserAuth userAuth, UUID orderId) {
		// orderId에 해당하는 payment가 있는지
		Order order = orderService.findByOrder(orderId);
		if (!order.getUser().getId().equals(userAuth.getId())) {
			throw new RuntimeException("해당 유저의 주문이 아닙니다.");
		}

		Payment payment = findByPayment(order);

		// payment -> responseDto
		return PaymentDetailResponseDto.from(payment);
	}

	@Override
	public AdminPaymentDetailResponseDto getAdminPaymentDetails(UUID orderId) {
		Order order = orderService.findByOrder(orderId);
		Payment payment = findByPayment(order);

		return AdminPaymentDetailResponseDto.from(payment);
	}
}
