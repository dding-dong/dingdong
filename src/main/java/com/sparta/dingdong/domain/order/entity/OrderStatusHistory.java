package com.sparta.dingdong.domain.order.entity;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_order_status_history")
public class OrderStatusHistory extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //추가
    @Column(nullable = false)
    private LocalDateTime changedAt;

    public static OrderStatusHistory create(Order order, OrderStatus status) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.order = order;
        history.status = status;
        history.changedAt = LocalDateTime.now();
        return history;
    }

    public static OrderStatusHistory create(Order order, OrderStatus status, Long changedByUserId) {
        OrderStatusHistory history = create(order, status);
        if (changedByUserId != null) {
            history.setCreatedBy(changedByUserId);
            history.setUpdatedBy(changedByUserId);
        }
        return history;
    }
}
