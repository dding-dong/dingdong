package com.sparta.dingdong.domain.cart.service;

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
import com.sparta.dingdong.domain.menu.exception.MenuItemNotFoundException;
import com.sparta.dingdong.domain.menu.exception.MenuItemSoldOutException;
import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCart(UserAuth userAuth) {
        User user = userService.findByUser(userAuth);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(CartNotFoundException::new);

        return CartResponseDto.from(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addItem(UserAuth userAuth, AddCartItemRequestDto req, boolean replace) {
        User user = userService.findByUser(userAuth);

        MenuItem menu = menuItemRepository.findById(req.getMenuItemId())
                .orElseThrow(MenuItemNotFoundException::new);

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

        // 카트에 아이템 합산 또는 신규 추가
        cart.addItem(CartItem.of(menu, req.getQuantity()));
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

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(CartNotFoundException::new);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        if (quantity <= 0) {
            throw new InvalidCartQuantityException();
        } else {
            item.updateQuantity(quantity);
        }

        Cart saved = cartRepository.save(cart);
        return CartResponseDto.from(saved);
    }

    @Override
    @Transactional
    public void removeItem(UserAuth userAuth, UUID menuItemId) {
        User user = userService.findByUser(userAuth);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(CartNotFoundException::new);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        cart.removeItem(item.getId());
        if (cart.getItems().isEmpty()) { // 장바구니에 메뉴가 없으면 장바구니 삭제
            cartRepository.delete(cart);
        } else {
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public void clearCart(UserAuth userAuth) {
        User user = userService.findByUser(userAuth);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(CartNotFoundException::new);
        cartRepository.delete(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart findByCart(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException());
    }

    @Override
    @Transactional
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
    }

}
