package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.domain.order.entity.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponseDto {
	private UUID menuItemId;
	private String menuItemName;
	private BigInteger unitPrice;
	private Integer quantity;

	public static OrderItemResponseDto from(OrderItem item) {
		return OrderItemResponseDto.builder()
			.menuItemId(item.getMenuItem().getId())
			.menuItemName(item.getMenuItem().getName())
			.unitPrice(item.getUnitPrice())
			.quantity(item.getQuantity())
			.build();
	}
}
