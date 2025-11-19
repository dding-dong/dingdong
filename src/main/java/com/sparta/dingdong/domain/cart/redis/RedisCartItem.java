package com.sparta.dingdong.domain.cart.redis;

import com.sparta.dingdong.domain.cart.entity.CartItem;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisCartItem implements Serializable {

    private static final long serialVersionUID = 2L;

    private UUID menuItemId;
    private String menuName;
    private BigInteger unitPrice;
    private int quantity;

    public static RedisCartItem fromEntity(CartItem ci) {
        return new RedisCartItem(
            ci.getMenuItem().getId(),
            ci.getMenuItem().getName(),
            ci.getMenuItem().getPrice(),
            ci.getQuantity()
        );
    }
}
