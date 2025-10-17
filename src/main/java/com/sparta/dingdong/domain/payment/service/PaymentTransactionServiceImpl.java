package com.sparta.dingdong.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.FailReason;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.exception.PaymentNotFoundException;
import com.sparta.dingdong.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

	private final PaymentRepository paymentRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markPaymentFailed(Payment payment) {
		payment.changeStatus(PaymentStatus.FAILED);
		payment.setFailReason(FailReason.PAY_PROCESS_ABORTED);
		paymentRepository.save(payment);
	}

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
}
