package com.sparta.dingdong.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentPageRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.ConfirmPaymentPageResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.TossCancelResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;
import com.sparta.dingdong.domain.payment.exception.NotOwnerPaymentException;
import com.sparta.dingdong.domain.payment.exception.NotTossPaymentOrderException;
import com.sparta.dingdong.domain.payment.exception.PaymentAlreadyExistsException;
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
	private final PaymentTransactionService paymentTransactionService;

	private Payment findPaymentByOrder(Order order) {
		return paymentRepository.findByOrder(order).orElse(null);
	}

	private Payment findPaymentByOrderOrThrow(Order order) {
		return paymentRepository.findByOrder(order).orElseThrow(PaymentNotFoundException::new);
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

		if (!findPaymentByOrder(order).getPaymentStatus().equals(PaymentStatus.PENDING)) {
			throw new TossConfirmPageException("ALREADY_ORDER_ID", "이미 결제한 주문입니다.");
		}

		Payment payment = findPaymentByOrderOrThrow(order);

		if (!payment.getAmount().equals(requestDto.getAmount())) {
			throw new TossConfirmPageException("MISS_MATCH_AMOUNT", "요청한 값이랑 결제 값이 다릅니다");
		}

		// Toss 결제 승인 요청 → 실패 시 TossPaymentWebClient에서 예외 발생함
		ConfirmPaymentPageResponseDto tossConfirmResponseDto = null;

		try {
			tossConfirmResponseDto = tossPaymentWebClient.confirmPayment(requestDto);

		} catch (TossConfirmPageException ex) {
			paymentTransactionService.markPaymentFailed(payment);
			throw new TossConfirmPageException(ex.getErrorCode(), ex.getErrorMessage());
		}

		// 결제 엔티티 수정

		payment.setTossOrderId(orderIdWithTimestamp);
		payment.setApprovedAt(tossConfirmResponseDto.getApprovedAt());
		payment.changeStatus(PaymentStatus.PAID);
		order.changeStatus(OrderStatus.REQUESTED);
		payment.confirmSuccess(tossConfirmResponseDto.getPaymentKey(),
			PaymentType.from(tossConfirmResponseDto.getType()));

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

	private Payment findByPaymentOrNull(Order order) {
		return paymentRepository.findByOrder(order).orElse(null);
	}

	@Override
	@Transactional
	public void createPayment(User user, Order order) {

		// 주문에 대한 결제가 이미 있으면 예외 처리해준다.
		Payment existingPayment = findByPaymentOrNull(order);

		// order에 대한 payment가 있는데 paymentStatus가 Failed 상태가 아니면 PaymentAlreadyExistsException();
		// order에 대한 payment가 있는데 paymentStatus가 Failed면 Pending상태로 바꿔준다.
		if (existingPayment != null) {

			if (!existingPayment.getPaymentStatus().equals(PaymentStatus.FAILED)) {
				throw new PaymentAlreadyExistsException();

			} else {
				existingPayment.deleteFailReason();
				existingPayment.changeStatus(PaymentStatus.PENDING);
				paymentRepository.save(existingPayment);
				return;
			}
		}

		// 이 두개를 짬뽕해서 Payment로 만들어준다. Payment 상태값을 Pending으로 저장.
		Payment payment = Payment.createPayment(user, order);

		// Payment 저장
		paymentRepository.save(payment);
	}
}
