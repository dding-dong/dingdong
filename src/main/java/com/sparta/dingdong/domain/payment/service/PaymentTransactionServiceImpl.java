package com.sparta.dingdong.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.FailReason;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
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
}
