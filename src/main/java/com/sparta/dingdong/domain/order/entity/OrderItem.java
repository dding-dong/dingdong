package com.sparta.dingdong.domain.order.entity;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "p_order_item")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private BigInteger unitPrice;

    @Column(nullable = false)
    private Integer quantity;
}
