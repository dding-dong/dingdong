package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentPageServiceImpl implements PaymentPageService {

	private final TossPaymentWebClient tossPaymentWebClient;
	private final PaymentRepository paymentRepository;
	private final OrderService orderService;

	@Transactional
	@Override
	public ConfirmPaymentPageResponseDto confirmPayment(ConfirmPaymentPageRequestDto requestDto) {

		UUID realOrderId = UUID.fromString(requestDto.getOrderId().split("_")[0]);
		Order order = orderService.findByOrder(realOrderId);

		ConfirmPaymentPageResponseDto tossConfirmResponseDto = null;
		try {

			tossConfirmResponseDto = tossPaymentWebClient.confirmPayment(requestDto);

		} catch (Exception e) {

			System.out.println(e.getMessage());

			return tossConfirmResponseDto;
		}

		// 이 두개를 짬뽕해서 Payment로 만들어준다. Payment 상태값을 Pending으로 저장.
		Payment payment = Payment.createPayment(order.getUser(), order, tossConfirmResponseDto);

		// Payment 저장
		paymentRepository.save(payment);

		return tossConfirmResponseDto;
	}
}
