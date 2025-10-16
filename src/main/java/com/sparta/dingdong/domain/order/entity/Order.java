package com.sparta.dingdong.domain.order.entity;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigInteger totalPrice;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private LocalDateTime placedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime canceledAt;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column
    private String cancelReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    //추가
    public static Order create(User user, Store store, BigInteger totalPrice, String deliveryAddress,
                               OrderStatus status) {
        Order order = new Order();
        order.user = user;
        order.store = store;
        order.totalPrice = totalPrice;
        order.deliveryAddress = deliveryAddress;
        order.status = status;
        order.placedAt = LocalDateTime.now();

        OrderStatusHistory history = OrderStatusHistory.create(order, status);
        order.statusHistories.add(history);

        return order;
    }

    public void changeStatus(OrderStatus newStatus, Long changedByUserId) {
        if (this.status == newStatus) return;
        this.status = newStatus;
        this.statusHistories.add(OrderStatusHistory.create(this, newStatus, changedByUserId));
    }

    public void changeStatus(OrderStatus newStatus) {
        changeStatus(newStatus, null);
    }

    public void cancel(String reason, Long changedByUserId) {
        this.status = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
        this.statusHistories.add(OrderStatusHistory.create(this, OrderStatus.CANCELED, changedByUserId));
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

}
