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
import com.sparta.dingdong.domain.cart.redis.RedisCartItem;
import com.sparta.dingdong.domain.cart.redis.RedisCartMeta;
import com.sparta.dingdong.domain.cart.redis.RedisKeys;
import com.sparta.dingdong.domain.cart.repository.CartRepository;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.exception.MenuItemSoldOutException;
import com.sparta.dingdong.domain.menu.service.MenuItemService;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MenuItemService menuItemService;
    private final UserService userService;

    private final RedisTemplate<String, Object> cartHashRedisTemplate;


    @Override
    @Transactional(readOnly = true)
    public Cart findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(CartNotFoundException::new);
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
        Long userId = userAuth.getId();
        String cartKey = RedisKeys.getCartKey(userId);
        String cartMetaKey = RedisKeys.getCartMetaKey(userId);

        // Redis Hash에서 모든 항목 조회 (HGETALL)
        Map<Object, Object> hashEntries = cartHashRedisTemplate.opsForHash().entries(cartKey);

        if (!hashEntries.isEmpty()) {
            // 메타 정보 조회
            RedisCartMeta meta = (RedisCartMeta) cartHashRedisTemplate.opsForValue()
                .get(cartMetaKey);
            log.info("meta: {}", meta);

            // 메타 정보도 있다면 완전한 캐시 히트
            if (meta != null) {
                List<RedisCartItem> redisItems = hashEntries.values().stream()
                    .filter(o -> o instanceof RedisCartItem)
                    .map(o -> (RedisCartItem) o)
                    .collect(Collectors.toList());

                return CartResponseDto.fromRedisItems(meta, redisItems);
            }
        }

        // 캐시 미스 (Cache Miss) 또는 Redis에 데이터가 없을 경우 DB 조회 (Read-Through)
        Cart dbCart = findByUserId(userId);

        //  DB 데이터로 Redis Hash를 채움 (Cache Warming)
        Map<String, RedisCartItem> redisMap = new HashMap<>();
        dbCart.getItems().forEach(ci -> {
            RedisCartItem redisItem = RedisCartItem.fromEntity(ci);
            redisMap.put(ci.getMenuItem().getId().toString(), redisItem);
        });

        if (!redisMap.isEmpty()) {
            cartHashRedisTemplate.opsForHash().putAll(cartKey, redisMap); // 새로 갱신

            // ✅ 메타 정보 저장 (누락된 로직 추가)
            RedisCartMeta meta = new RedisCartMeta(dbCart.getStore().getId(),
                dbCart.getStore().getName(),
                dbCart.getId());
            cartHashRedisTemplate.opsForValue().set(cartMetaKey, meta);

            // TTL 설정
            cartHashRedisTemplate.expire(cartKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
            cartHashRedisTemplate.expire(cartMetaKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
        }

        return CartResponseDto.from(dbCart);
    }

    @Override
    @Transactional
    public CartResponseDto addItem(UserAuth userAuth, AddCartItemRequestDto req, boolean replace) {
        User user = userService.findByUser(userAuth);

        String cartKey = RedisKeys.getCartKey(user.getId());
        String cartMetaKey = RedisKeys.getCartMetaKey(user.getId());

        MenuItem menu = menuItemService.findById(req.getMenuItemId());
        if (menu.getIsSoldout()) {
            throw new MenuItemSoldOutException();
        }
        UUID menuId = menu.getId();

        // 장바구니가 없는 경우 새 생성
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElse(Cart.of(user, menu.getStore()));

        // 장바구니가 있고 다른 가게인 경우
        if (cart.getStore() != null && !cart.getStore().getId().equals(menu.getStore().getId())) {
            if (replace) {
                cart.clear();
                cart.setStore(menu.getStore());

                cartHashRedisTemplate.delete(cartKey);
                cartHashRedisTemplate.delete(cartMetaKey);
            } else {
                throw new CartStoreConflictException();
            }
        }

        // 이미 존재하면 수량 합산, 없으면 새 아이템 추가
        cart.addItem(menu, req.getQuantity());
        Cart savedCart = cartRepository.save(cart);

        // 장바구니 아이템 Entity에서 Redis용 스냅샷 DTO 생성
        CartItem addedItem = savedCart.getItems().stream()
            .filter(ci -> ci.getMenuItem().getId().equals(menuId))
            .findFirst().orElseThrow(CartItemNotFoundException::new);

        RedisCartItem redisItem = RedisCartItem.fromEntity(addedItem);

        // Hash 구조에 해당 필드만 갱신
        cartHashRedisTemplate.opsForHash().put(cartKey, menuId.toString(), redisItem);

        //  메타 정보 저장 (Write-Through)
        RedisCartMeta meta = new RedisCartMeta(savedCart.getStore().getId(),
            savedCart.getStore().getName(),
            savedCart.getId());
        cartHashRedisTemplate.opsForValue().set(cartMetaKey, meta);

        cartHashRedisTemplate.expire(cartKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
        cartHashRedisTemplate.expire(cartMetaKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);

        return CartResponseDto.from(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDto updateItemQuantity(UserAuth userAuth, UUID menuItemId, int quantity) {
        Long userId = userAuth.getId();
        String cartKey = RedisKeys.getCartKey(userId);
        String cartMetaKey = RedisKeys.getCartMetaKey(userId);

        Cart cart = findByUserId(userId);
        CartItem item = getCartItem(menuItemId, cart);

        if (quantity <= 0) {
            throw new InvalidCartQuantityException();
        }
        item.updateQuantity(quantity);
        Cart saved = cartRepository.save(cart);

        CartItem updatedItem = getCartItem(menuItemId, saved);
        RedisCartItem redisItem = RedisCartItem.fromEntity(updatedItem);

        cartHashRedisTemplate.opsForHash().put(cartKey, menuItemId.toString(), redisItem);

        cartHashRedisTemplate.expire(cartKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
        cartHashRedisTemplate.expire(cartMetaKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);

        return CartResponseDto.from(saved);
    }

    @Override
    @Transactional
    public void deleteItem(UserAuth userAuth, UUID menuItemId) {
        Long userId = userAuth.getId();
        String cartKey = RedisKeys.getCartKey(userId);
        String cartMetaKey = RedisKeys.getCartMetaKey(userId);

        Cart cart = findByUserId(userId);
        CartItem item = getCartItem(menuItemId, cart);

        cart.removeItem(item.getId());
        if (cart.getItems().isEmpty()) { // 장바구니에 메뉴가 없으면 장바구니 삭제
            cartRepository.delete(cart);

            cartHashRedisTemplate.delete(cartKey);
            cartHashRedisTemplate.delete(cartMetaKey);
        } else {
            cartRepository.save(cart);

            cartHashRedisTemplate.opsForHash().delete(cartKey, menuItemId.toString());

            cartHashRedisTemplate.expire(cartKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
            cartHashRedisTemplate.expire(cartMetaKey, RedisKeys.CART_TTL_MINUTES, TimeUnit.MINUTES);
        }
    }

    @Override
    @Transactional
    public void deleteCart(UserAuth userAuth) {
        Long userId = userAuth.getId();
        String cartKey = RedisKeys.getCartKey(userId);
        String cartMetaKey = RedisKeys.getCartMetaKey(userId);

        Cart cart = findByUserId(userId);
        cartRepository.delete(cart);

        cartHashRedisTemplate.delete(cartKey);
        cartHashRedisTemplate.delete(cartMetaKey);
    }
}
