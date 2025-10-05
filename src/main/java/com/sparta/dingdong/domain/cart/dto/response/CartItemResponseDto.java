package com.sparta.dingdong.domain.cart.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponseDto {
	private final UUID itemId;
	private final UUID menuItemId;
	private final String menuName;
	private final BigInteger unitPrice;
	private final int quantity;
	private final BigInteger totalPrice;
}
