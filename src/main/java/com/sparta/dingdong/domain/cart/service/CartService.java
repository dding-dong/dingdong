package com.sparta.dingdong.domain.cart.service;

import java.util.UUID;

import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;

public interface CartService {

	CartResponseDto getCart(Long userId);

	Object addItem(Long userId, AddCartItemRequestDto dto, boolean replace);

	CartResponseDto updateItemQuantity(Long userId, UUID menuItemId, int quantity);

	void removeItem(Long userId, UUID menuItemId);

	void clearCart(Long userId);
}
