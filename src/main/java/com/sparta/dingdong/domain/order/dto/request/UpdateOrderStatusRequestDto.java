package com.sparta.dingdong.domain.order.dto.request;

import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDto {
	private OrderStatus newStatus;
}