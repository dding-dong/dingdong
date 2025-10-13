package com.sparta.dingdong.domain.cart.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.cart.entity.CartItem;

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

	public static CartItemResponseDto fromEntity(CartItem i) {
		return CartItemResponseDto.builder()
			.itemId(i.getId())
			.menuItemId(i.getMenuItem().getId())
			.menuName(i.getMenuItem().getName())
			.unitPrice(i.getMenuItem().getPrice())
			.quantity(i.getQuantity())
			.totalPrice(i.getMenuItem().getPrice().multiply(BigInteger.valueOf(i.getQuantity())))
			.build();
	}
}
