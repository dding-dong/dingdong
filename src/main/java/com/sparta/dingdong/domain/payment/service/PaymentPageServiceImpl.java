package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.TossCancelResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.exception.NotOwnerPaymentException;
import com.sparta.dingdong.domain.payment.exception.NotTossPaymentOrderException;
import com.sparta.dingdong.domain.payment.exception.PaymentNotFoundException;
import com.sparta.dingdong.domain.payment.exception.TossConfirmPageException;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentPageServiceImpl implements PaymentPageService {

	private final TossPaymentWebClient tossPaymentWebClient;
	private final PaymentRepository paymentRepository;
	private final OrderService orderService;
	private final UserService userService;

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

	@Override
	@Transactional
	public TossCancelResponseDto cancelPayment(CancelPaymentRequestDto requestDto, UserAuth userAuth) {
		User user = userService.findByUser(userAuth);

		Order order = orderService.findByOrder(requestDto.getOrderId());
		Payment payment = findPaymentByOrder(order);

		if (!user.equals(order.getUser())) {
			throw new NotOwnerPaymentException("해당 유저의 결제가 아닙니다");
		}

		if (payment == null) {
			throw new PaymentNotFoundException();
		}

		if (payment.getTossOrderId() == null) {
			throw new NotTossPaymentOrderException("토스 페에지에서 결제한 주문이 아닙니다.");
		}

		CancelPaymentPageRequestDto tossCancelRequestDto = CancelPaymentPageRequestDto.builder()
			.cancelReason(requestDto.getRefundReason())
			.build();

		TossCancelResponseDto tossCancelResponseDto = tossPaymentWebClient.cancelPayment(tossCancelRequestDto,
			payment.getPaymentKey());

		payment.changeStatus(PaymentStatus.REFUNDED);
		payment.setRefundReason(requestDto.getRefundReason());

		return tossCancelResponseDto;
	}
}
