package com.sparta.dingdong.domain.cart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDto {
	private int quantity;
}
