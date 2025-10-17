package com.sparta.dingdong.domain.cart.service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.entity.Cart;

import java.util.UUID;

public interface CartService {

    CartResponseDto getCart(UserAuth userAuth);

    CartResponseDto addItem(UserAuth userAuth, AddCartItemRequestDto req, boolean replace);

    CartResponseDto updateItemQuantity(UserAuth userAuth, UUID menuItemId, int quantity);

    void removeItem(UserAuth userAuth, UUID menuItemId);

    void clearCart(UserAuth userAuth);

    Cart findByCart(UUID cartId);

    void deleteCart(Cart cart);
}
