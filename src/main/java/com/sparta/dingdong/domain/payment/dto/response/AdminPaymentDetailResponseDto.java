package com.sparta.dingdong.domain.payment.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.dingdong.domain.payment.entity.Payment;
import com.sparta.dingdong.domain.payment.entity.enums.FailReason;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPaymentDetailResponseDto {
	private UUID paymentId;
	private Long userId;

	private UUID orderId;

	private PaymentType paymentType;

	private BigInteger amount;
	private PaymentStatus paymentStatus;

	private String paymentKey;

	private FailReason failReason;

	private String refundReason;

	private LocalDateTime approvedAt;

	private LocalDateTime createdAt;

	private Long createdBy;

	private LocalDateTime updatedAt;

	private Long updatedBy;

	private LocalDateTime deletedAt;

	private Long deletedBy;

	public static AdminPaymentDetailResponseDto from(Payment payment) {
		return AdminPaymentDetailResponseDto.builder()
			.paymentId(payment.getId())
			.userId(payment.getUser().getId())
			.orderId(payment.getOrder().getId())
			.paymentType(payment.getPaymentType())
			.amount(payment.getAmount())
			.paymentStatus(payment.getPaymentStatus())
			.paymentKey(payment.getPaymentKey())
			.failReason(payment.getFailReason())
			.refundReason(payment.getRefundReason())
			.approvedAt(payment.getApprovedAt())
			.createdAt(payment.getCreatedAt())
			.createdBy(payment.getCreatedBy())
			.updatedAt(payment.getUpdatedAt())
			.updatedBy(payment.getUpdatedBy())
			.deletedAt(payment.getDeletedAt())
			.deletedBy(payment.getDeletedBy())
			.build();
	}

}
