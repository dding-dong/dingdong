package com.sparta.dingdong.domain.payment.entity;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.entity.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	private String payMethod;

	private BigInteger price;

	private String pgTxId;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
}
