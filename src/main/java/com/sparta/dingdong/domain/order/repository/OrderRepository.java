package com.sparta.dingdong.domain.order.repository;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"store"})
    List<Order> findAllByUserId(Long userId); //Long

    @EntityGraph(attributePaths = {"user"})
    List<Order> findAllByStoreId(UUID storeId);

    @EntityGraph(attributePaths = {"store", "orderItems", "orderItems.menuItem"})
    Order findWithDetailsById(UUID orderId);

    List<Order> findAllByUserIdAndStatus(Long userId, OrderStatus status);

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByStoreIdAndStoreOwnerIdAndStatus(UUID storeId, Long ownerId, OrderStatus status);

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByStoreIdAndStoreOwnerId(UUID storeId, Long ownerId);

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByStoreOwnerIdAndStatus(Long ownerId, OrderStatus status);

    @EntityGraph(attributePaths = {"user", "store"})
    List<Order> findAllByStoreOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"user", "store"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store"})
    Page<Order> findAllByStoreId(UUID storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store"})
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store"})
    Page<Order> findAllByStoreIdAndStatus(UUID storeId, OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store"})
    Page<Order> findAllByIdEquals(UUID orderId, Pageable pageable);
}
