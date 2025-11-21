package com.sparta.dingdong.domain.cart.dto.response;

import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.redis.RedisCartItem;
import com.sparta.dingdong.domain.cart.redis.RedisCartMeta;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {

    private UUID cartId;
    private UUID storeId;
    private String storeName;
    private List<CartItemResponseDto> items;
    private BigInteger totalPrice;
    private String message;

    public static CartResponseDto from(Cart cart) {
        List<CartItemResponseDto> items = cart.getItems().stream()
            .map(CartItemResponseDto::fromEntity)
            .collect(Collectors.toList());

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

    public static CartResponseDto fromRedisItems(RedisCartMeta meta,
        List<RedisCartItem> redisItems) {
        if (redisItems == null || redisItems.isEmpty()) {
            return CartResponseDto.builder().items(List.of()).totalPrice(BigInteger.ZERO).build();
        }

        // 1. 개별 아이템 DTO로 변환
        List<CartItemResponseDto> items = redisItems.stream()
            .map(CartItemResponseDto::fromRedisItem)
            .collect(Collectors.toList());

        // 2. 총 가격 계산
        BigInteger total = items.stream()
            .map(CartItemResponseDto::getTotalPrice)
            .reduce(BigInteger.ZERO, BigInteger::add);

        // 3. 메타 정보 사용
        return CartResponseDto.builder()
            .cartId(meta.getCartId())
            .storeId(meta.getStoreId())
            .storeName(meta.getStoreName())
            .items(items)
            .totalPrice(total)
            .build();
    }

}
