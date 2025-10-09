package com.sparta.dingdong.domain.payment.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.payment.dto.request.ConfirmPaymentsRequestDto;
import com.sparta.dingdong.domain.payment.dto.request.PaymentRequestDto;
import com.sparta.dingdong.domain.payment.dto.response.FakeTossPaymentFailResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.dingdong.domain.payment.dto.response.TossPaymentResponseDto;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.exception.PaymentAlreadyExistsException;
import com.sparta.dingdong.domain.payment.exception.PaymentAmountMismatchException;
import com.sparta.dingdong.domain.payment.exception.PaymentNotFoundException;
import com.sparta.dingdong.domain.payment.exception.PaymentStatusNoPendingException;
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
	private final PaymentTransactionService paymentTransactionService;

	private Payment findByPayment(Order order) {
		return paymentRepository.findByOrder(order).orElseThrow(PaymentNotFoundException::new);
	}

	private Payment findByPaymentOrNull(Order order) {
		return paymentRepository.findByOrder(order).orElse(null);
	}

	@Override
	@Transactional
	public PaymentResponseDto createPayment(UserAuth userAuth, PaymentRequestDto request) {
		// User 가져온다
		User user = userService.findByUser(userAuth);

		// Order 가져온다
		Order order = orderService.findByOrder(request.getOrderId());

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
				return PaymentResponseDto.from(existingPayment, request);
			}
		}

		// 이 두개를 짬뽕해서 Payment로 만들어준다. Payment 상태값을 Pending으로 저장.
		Payment payment = Payment.createPayment(user, order);

		// Payment 저장
		Payment savePayment = paymentRepository.save(payment);

		return PaymentResponseDto.from(savePayment, request);
	}

	@Override
	@Transactional
	public void createConfirmPayments(UserAuth userAuth, ConfirmPaymentsRequestDto request) {
		// Order Payment 찾고
		Order order = orderService.findByOrder(request.getOrderId());

		Payment payment = findByPayment(order);

		// pending이 아닌데 요청하면 exception(결제 요청으로 다시해주세요)
		if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
			throw new PaymentStatusNoPendingException();
		}

		// Payment amount 값이랑 request amount 값 같은지 확인 값 다르면 exception 날려주기
		// 결제 금액 확인
		if (!Objects.equals(payment.getAmount(), request.getAmount())) {
			// 새 트랜잭션에서 실패 상태 저장
			paymentTransactionService.markPaymentFailed(payment);

			// 예외 발생
			throw new PaymentAmountMismatchException();
		}

		fakeTossCall(request, order, payment);

		// Payment Paid 로 상태 변경 && Order Status 변경
		payment.changeStatus(PaymentStatus.PAID);
		order.changeStatus(OrderStatus.REQUESTED);

		// payment 저장 PaymentKey, PaymentType
		payment.confirmSuccess(request.getPaymentKey(), request.getPaymentType());

		paymentRepository.save(payment);
	}

	private void fakeTossCall(ConfirmPaymentsRequestDto request, Order order, Payment payment) {
		// 여기서 https://api.tosspayments.com/v1/payments/confirm orderId, amount, paymentKey 담아서 가상 요청
		new Thread(() -> {
			try {
				System.out.println(
					"[Toss] 요청 전송 중... orderId=" + request.getOrderId() + "amount=" + order.getTotalPrice());
				Thread.sleep(1500); // 1.5초 대기 (요청 시뮬레이션)
				System.out.println("[Toss] 요청 완료. 응답 수신 성공");
				// successURL로 갔을 때

				// 더미 응답 객체
				TossPaymentResponseDto fakeResponse = new TossPaymentResponseDto(
					request.getOrderId(),
					"DONE",
					"fake-payment-key-123"
				);

				// 응답 후 처리 로직 (ex. 로그 남기기)
				System.out.println("[Toss] 응답: " + fakeResponse);

				// 테스트용 강제 예외 발생
				// throw new InterruptedException("테스트 결제 실패");

			} catch (InterruptedException e) { // failURL로 갔을 때
				System.out.println("[Toss] 결제 실패: " + e.getMessage());

				// fail?code={ERROR_CODE}&message={ERROR_MESSAGE} & orderId={ORDER_ID}
				FakeTossPaymentFailResponseDto fakeTossPaymentFailResponseDto = new FakeTossPaymentFailResponseDto(
					order.getId(),
					"500",
					"NOT_FOUND_PAYMENT_SESSION"
				);

				paymentTransactionService.markPaymentFailed(payment);
			}
		}).start();
	}
}
