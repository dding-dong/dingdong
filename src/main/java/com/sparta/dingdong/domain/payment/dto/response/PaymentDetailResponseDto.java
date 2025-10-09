package com.sparta.dingdong.domain.payment.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.FailReason;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailResponseDto {
	private BigInteger amount;
	private PaymentStatus paymentStatus;

	private FailReason failReason;

	private String refundReason;

	private LocalDateTime approvedAt;

	public static PaymentDetailResponseDto from(Payment payment) {
		return PaymentDetailResponseDto.builder()
			.amount(payment.getAmount())
			.paymentStatus(payment.getPaymentStatus())
			.failReason(payment.getFailReason())
			.refundReason(payment.getRefundReason())
			.approvedAt(payment.getApprovedAt())
			.build();
	}

}
