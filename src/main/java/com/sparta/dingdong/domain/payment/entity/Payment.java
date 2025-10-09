package com.sparta.dingdong.domain.payment.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.entity.enums.FailReason;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;
import com.sparta.dingdong.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	private BigInteger amount;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column(name = "payment_key")
	private String paymentKey;

	@Enumerated(EnumType.STRING)
	private FailReason failReason;

	private String refundReason;

	private LocalDateTime approvedAt;

	public static Payment createPayment(User user, Order order) {
		return Payment.builder()
			.user(user)
			.order(order)
			.amount(order.getTotalPrice())
			.paymentStatus(PaymentStatus.PENDING)
			.build();
	}
}
