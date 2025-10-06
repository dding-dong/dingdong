package com.sparta.dingdong.domain.payment.dto.request;

import java.math.BigInteger;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequestDto {
	@NotNull
	private final UUID orderId;

	@NotNull
	private final BigInteger amount;

	@NotBlank
	private final String paymentMethod;
}
