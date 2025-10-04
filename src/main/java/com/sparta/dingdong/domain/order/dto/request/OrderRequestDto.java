package com.sparta.dingdong.domain.order.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDto {

	@NotNull
	private final UUID cartId;

	@NotBlank
	private final String deliveryAddress;

	@NotBlank
	private final String paymentMethod;

}
