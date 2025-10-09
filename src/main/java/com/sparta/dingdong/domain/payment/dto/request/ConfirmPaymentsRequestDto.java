package com.sparta.dingdong.domain.payment.dto.request;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.payment.entity.enums.PaymentType;

import lombok.Getter;

@Getter
public class ConfirmPaymentsRequestDto {

	private UUID orderId;

	private BigInteger amount;

	private String paymentKey;

	private PaymentType paymentType;
}
