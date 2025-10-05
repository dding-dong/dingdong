package com.sparta.dingdong.domain.cart.dto.response;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartResponseDto {
	private final UUID cartId;
	private final UUID storeId;
	private final String storeName;
	private final List<CartItemResponseDto> items;
	private final BigInteger totalPrice;
	private final String message;

}
