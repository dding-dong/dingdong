package com.sparta.dingdong.domain.cart.redis;

import java.io.Serializable;
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
public class RedisCartMeta implements Serializable {

    private static final long serialVersionUID = 3L;
    private UUID storeId;
    private String storeName;
    private UUID cartId;

}
