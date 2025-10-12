package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.exception.TossConfirmPageException;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentPageServiceImpl implements PaymentPageService {

	private final TossPaymentWebClient tossPaymentWebClient;
	private final PaymentRepository paymentRepository;
	private final OrderService orderService;

	private Payment findPaymentByOrder(Order order) {
		return paymentRepository.findByOrder(order).orElse(null);
	}

	@Transactional
	@Override
	public ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto) {
		String orderIdWithTimestamp = requestDto.getOrderId();

		if (orderIdWithTimestamp == null || !orderIdWithTimestamp.contains("_")) {
			throw new TossConfirmPageException("WRONG_ORDER_ID", orderIdWithTimestamp);
		}

		UUID realOrderId = UUID.fromString(orderIdWithTimestamp.split("_")[0]);
		Order order = orderService.findByOrder(realOrderId);

		if (findPaymentByOrder(order) != null) {
			throw new TossConfirmPageException("ALREADY_ORDER_ID", "이미 결제한 주문입니다.");
		}

		// Toss 결제 승인 요청 → 실패 시 TossPaymentWebClient에서 예외 발생함
		ConfirmPaymentPageResponseDto tossConfirmResponseDto = tossPaymentWebClient.confirmPayment(requestDto);

		// 결제 엔티티 생성 및 저장
		Payment payment = Payment.createPayment(order.getUser(), order, tossConfirmResponseDto);
		paymentRepository.save(payment);

		return tossConfirmResponseDto;
	}
}
