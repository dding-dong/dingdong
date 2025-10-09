package com.sparta.dingdong.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.PaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.exception.PaymentAlreadyExistsException;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final UserService userService;
	private final OrderService orderService;

	private boolean findPaymentByOrder(Order order) {
		return paymentRepository.findByOrder(order).isPresent();
	}

	@Override
	@Transactional
	public PaymentResponseDto createPayment(UserAuth userAuth, PaymentRequestDto request) {
		// User 가져온다
		User user = userService.findByUser(userAuth);

		// Order 가져온다
		Order order = orderService.findByOrder(request.getOrderId());

		// 주문에 대한 결제가 이미 있으면 예외 처리해준다.
		if (findPaymentByOrder(order)) {
			throw new PaymentAlreadyExistsException();
		}

		// 이 두개를 짬뽕해서 Payment로 만들어준다. Payment 상태값을 Pending으로 저장.
		Payment payment = Payment.createPayment(user, order);

		// Payment 저장
		Payment savePayment = paymentRepository.save(payment);

		return PaymentResponseDto.from(savePayment, request);
	}
}
