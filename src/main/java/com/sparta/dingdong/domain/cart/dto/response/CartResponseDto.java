package com.sparta.dingdong.domain.cart.dto.response;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sparta.dingdong.domain.cart.entity.Cart;

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

	public static CartResponseDto from(Cart cart) {
		List<CartItemResponseDto> items = cart.getItems().stream()
			.map(CartItemResponseDto::fromEntity)
			.collect(Collectors.toList());

		BigInteger total = items.stream()
			.map(CartItemResponseDto::getTotalPrice)
			.reduce(BigInteger.ZERO, BigInteger::add);

		return CartResponseDto.builder()
			.cartId(cart.getId())
			.storeId(cart.getStore().getId())
			.storeName(cart.getStore().getName())
			.items(items)
			.totalPrice(total)
			.build();
	}

}
