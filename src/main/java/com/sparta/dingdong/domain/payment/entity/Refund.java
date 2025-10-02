package com.sparta.dingdong.domain.payment.entity;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_refund")
public class Refund extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@OneToOne
	@JoinColumn(name = "payment_id")
	private Payment payment;

	private BigInteger price;

	private String reason;
}
