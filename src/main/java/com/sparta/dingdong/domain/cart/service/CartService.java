package com.sparta.dingdong.domain.cart.service;

import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.entity.Cart;

public interface CartService {

	Cart findByUserId(Long userId);

	CartResponseDto getCart(UserAuth userAuth);

	CartResponseDto addItem(UserAuth userAuth, AddCartItemRequestDto req, boolean replace);

	CartResponseDto updateItemQuantity(UserAuth userAuth, UUID menuItemId, int quantity);

	void deleteItem(UserAuth userAuth, UUID menuItemId);

	void deleteCart(UserAuth userAuth);
}
