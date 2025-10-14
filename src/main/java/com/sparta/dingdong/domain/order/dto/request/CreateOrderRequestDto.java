package com.sparta.dingdong.domain.order.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
	private UUID cartId;
	private String deliveryAddress;
}
