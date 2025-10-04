package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponseDto {
	private final UUID itemId;
	private final UUID menuItemId;
	private final String menuName;
	private final BigInteger unitPrice;
	private final int quantity;
	private final BigInteger totalPrice;
}
