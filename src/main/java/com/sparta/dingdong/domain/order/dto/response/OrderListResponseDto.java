package com.sparta.dingdong.domain.order.dto.response;

import java.util.List;

import com.sparta.dingdong.domain.order.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderListResponseDto {
	private List<OrderResponseDto> orders;

	public static OrderListResponseDto from(Order order) {
		return new OrderListResponseDto(List.of(OrderResponseDto.from(order)));
	}
}
