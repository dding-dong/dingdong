package com.sparta.dingdong.domain.order.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderListResponseDto {
	private List<OrderResponseDto> orders;
}

