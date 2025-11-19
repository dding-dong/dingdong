package com.sparta.dingdong.domain.cart.redis;

import java.util.concurrent.TimeUnit;

public final class RedisKeys {

    public static final long CART_TTL_MINUTES = 30;
    public static final TimeUnit CART_TTL_UNIT = TimeUnit.MINUTES;

    // 키 패턴 정의
    private static final String CART_KEY_FORMAT = "cart:user:%d";
    private static final String CART_META_KEY_FORMAT = "cart:meta:%d";

    private RedisKeys() {
    }

    public static String getCartKey(Long userId) {
        return String.format(CART_KEY_FORMAT, userId);
    }

    public static String getCartMetaKey(Long userId) {
        return String.format(CART_META_KEY_FORMAT, userId);
    }
}
