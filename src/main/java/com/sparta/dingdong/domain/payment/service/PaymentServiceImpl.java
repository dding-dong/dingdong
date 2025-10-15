package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.response.AdminPaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentDetailResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
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
	}// orderException 만들 때 이거 까지 추가

	@Override
	public AdminPaymentDetailResponseDto getAdminPaymentDetails(UUID orderId) {
		Order order = orderService.findByOrder(orderId);
		Payment payment = findByPayment(order);

		return AdminPaymentDetailResponseDto.from(payment);
	}
}
