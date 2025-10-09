package com.sparta.dingdong.domain.cart.service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartConflictResponseDto;
import com.sparta.dingdong.domain.cart.dto.response.CartItemResponseDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.entity.CartItem;
import com.sparta.dingdong.domain.cart.exception.CartNotFoundException;
import com.sparta.dingdong.domain.cart.repository.CartItemRepository;
import com.sparta.dingdong.domain.cart.repository.CartRepository;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.exception.MenuItemNotFoundException;
import com.sparta.dingdong.domain.menu.exception.MenuItemSoldOutException;
import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final MenuItemRepository menuItemRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public CartResponseDto getCart(Long userId) {
		Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
		if (cartOpt.isEmpty()) {
			return CartResponseDto.builder()
				.items(Collections.emptyList())
				.totalPrice(BigInteger.ZERO)
				.message("장바구니가 비어 있습니다.")
				.build();
		}

		return toDto(cartOpt.get());
	}

	@Override
	@Transactional
	public Object addItem(Long userId, AddCartItemRequestDto req, boolean replace) {
		MenuItem menu = menuItemRepository.findById(req.getMenuItemId())
			.orElseThrow(MenuItemNotFoundException::new);

		if (menu.getIsSoldout()) {
			throw new MenuItemSoldOutException();
		}

		Cart cart = cartRepository.findByUserId(userId).orElse(null);

		// 장바구니가 없는 경우 새 생성
		if (cart == null) {
			User user = userRepository.getReferenceById(userId);
			cart = Cart.of(user, menu.getStore());
		}

		// 장바구니가 있고 다른 가게인 경우
		if (cart.getStore() != null && !cart.getStore().getId().equals(menu.getStore().getId())) {
			if (replace) {
				cartRepository.delete(cart); // 기존 장바구니 삭제
				cartRepository.flush();
				User user = userRepository.getReferenceById(userId);
				cart = Cart.of(user, menu.getStore()); // 새 장바구니 생성
			} else {
				return CartConflictResponseDto.builder()
					.existingCartId(cart.getId())
					.existingStoreId(cart.getStore().getId())
					.existingStoreName(cart.getStore().getName())
					.newStoreId(menu.getStore().getId())
					.newStoreName(menu.getStore().getName())
					.build();
			}
		}

		cart.addItem(CartItem.of(menu, req.getQuantity()));
		Cart saved = cartRepository.save(cart);
		return toDto(saved);
	}

	@Override
	@Transactional
	public CartResponseDto updateItemQuantity(Long userId, UUID menuItemId, int quantity) {
		Cart cart = cartRepository.findByUserId(userId)
			.orElseThrow(CartNotFoundException::new);

		CartItem item = cart.getItems().stream()
			.filter(i -> i.getMenuItem().getId().equals(menuItemId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("CartItem not found"));

		if (quantity <= 0) {
			cart.removeItem(item.getId());

			if (cart.getItems().isEmpty()) { // 장바구니에 메뉴가 없으면 장바구니 삭제
				cartRepository.delete(cart);
			}
		} else {
			item.updateQuantity(quantity);
		}

		Cart saved = cartRepository.save(cart);
		return toDto(saved);
	}

	@Override
	@Transactional
	public void removeItem(Long userId, UUID menuItemId) {
		Cart cart = cartRepository.findByUserId(userId)
			.orElseThrow(CartNotFoundException::new);

		CartItem item = cart.getItems().stream()
			.filter(i -> i.getMenuItem().getId().equals(menuItemId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("CartItem not found"));

		cart.removeItem(item.getId());
		if (cart.getItems().isEmpty()) { // 장바구니에 메뉴가 없으면 장바구니 삭제
			cartRepository.delete(cart);
		} else {
			cartRepository.save(cart);
		}
	}

	@Override
	@Transactional
	public void clearCart(Long userId) {
		Cart cart = cartRepository.findByUserId(userId)
			.orElseThrow(CartNotFoundException::new);
		cartRepository.delete(cart);
	}

	/* ---------- helpers ---------- */
	private CartResponseDto toDto(Cart cart) {
		List<CartItemResponseDto> items = cart.getItems().stream().map(i ->
			CartItemResponseDto.builder()
				.itemId(i.getId())
				.menuItemId(i.getMenuItem().getId())
				.menuName(i.getMenuItem().getName())
				.unitPrice(i.getMenuItem().getPrice())
				.quantity(i.getQuantity())
				.totalPrice(i.getMenuItem().getPrice().multiply(BigInteger.valueOf(i.getQuantity())))
				.build()
		).collect(Collectors.toList());

		BigInteger total = items.stream()
			.map(CartItemResponseDto::getTotalPrice)
			.reduce(BigInteger.ZERO, BigInteger::add);

		return CartResponseDto.builder()
			.cartId(cart.getId())
			.storeId(cart.getStore().getId())
			.storeName(cart.getStore().getName())
			.items(items)
			.totalPrice(total)
			.build();
	}
}
