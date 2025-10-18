package com.sparta.dingdong.domain.cart.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.entity.CartItem;
import com.sparta.dingdong.domain.cart.exception.CartItemNotFoundException;
import com.sparta.dingdong.domain.cart.exception.CartNotFoundException;
import com.sparta.dingdong.domain.cart.exception.CartStoreConflictException;
import com.sparta.dingdong.domain.cart.exception.InvalidCartQuantityException;
import com.sparta.dingdong.domain.cart.repository.CartRepository;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.exception.MenuItemSoldOutException;
import com.sparta.dingdong.domain.menu.service.MenuItemService;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final MenuItemService menuItemService;
	private final UserService userService;

	@Override
	@Transactional(readOnly = true)
	public Cart findByUserId(Long userId) {
		return cartRepository.findByUserId(userId)
			.orElseThrow(() -> new CartNotFoundException());
	}

	private CartItem getCartItem(UUID menuItemId, Cart cart) {
		return cart.getItems().stream()
			.filter(i -> i.getMenuItem().getId().equals(menuItemId))
			.findFirst()
			.orElseThrow(CartItemNotFoundException::new);
	}

	@Override
	@Transactional(readOnly = true)
	public CartResponseDto getCart(UserAuth userAuth) {
		User user = userService.findByUser(userAuth);
		Cart cart = findByUserId(user.getId());

		return CartResponseDto.from(cart);
	}

	@Override
	@Transactional
	public CartResponseDto addItem(UserAuth userAuth, AddCartItemRequestDto req, boolean replace) {
		User user = userService.findByUser(userAuth);

		MenuItem menu = menuItemService.findById(req.getMenuItemId());
		if (menu.getIsSoldout()) {
			throw new MenuItemSoldOutException();
		}

		Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

		// 장바구니가 없는 경우 새 생성
		if (cart == null) {
			cart = createNewCartForUser(user, menu.getStore());
		}

		// 장바구니가 있고 다른 가게인 경우
		if (cart.getStore() != null && !cart.getStore().getId().equals(menu.getStore().getId())) {
			if (replace) {
				cart.clear();
				cart.setStore(menu.getStore());
			} else {
				throw new CartStoreConflictException();
			}
		}

		// 이미 존재하면 수량 합산, 없으면 새 아이템 추가
		cart.addItem(menu, req.getQuantity());
		Cart saved = cartRepository.save(cart);
		return CartResponseDto.from(saved);
	}

	private Cart createNewCartForUser(User user, Store store) {
		Cart newCart = Cart.of(user, store);
		return cartRepository.save(newCart);
	}

	@Override
	@Transactional
	public CartResponseDto updateItemQuantity(UserAuth userAuth, UUID menuItemId, int quantity) {
		User user = userService.findByUser(userAuth);
		Cart cart = findByUserId(user.getId());

		CartItem item = getCartItem(menuItemId, cart);

		if (quantity <= 0) {
			throw new InvalidCartQuantityException();
		}

		item.updateQuantity(quantity);
		Cart saved = cartRepository.save(cart);
		return CartResponseDto.from(saved);
	}

	@Override
	@Transactional
	public void deleteItem(UserAuth userAuth, UUID menuItemId) {
		User user = userService.findByUser(userAuth);
		Cart cart = findByUserId(user.getId());

		CartItem item = getCartItem(menuItemId, cart);

		cart.removeItem(item.getId());
		if (cart.getItems().isEmpty()) { // 장바구니에 메뉴가 없으면 장바구니 삭제
			cartRepository.delete(cart);
		} else {
			cartRepository.save(cart);
		}
	}

	@Override
	@Transactional
	public void deleteCart(UserAuth userAuth) {
		User user = userService.findByUser(userAuth);
		Cart cart = findByUserId(user.getId());

		cartRepository.delete(cart);
	}

}
