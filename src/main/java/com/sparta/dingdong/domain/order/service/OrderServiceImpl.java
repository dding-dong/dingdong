package com.sparta.dingdong.domain.order.service;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.service.CartService;
import com.sparta.dingdong.domain.order.dto.request.CreateOrderRequestDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderDetailResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderListResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderResponseDto;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.order.repository.OrderRepository;
import com.sparta.dingdong.domain.payment.service.PaymentTransactionService;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.repository.StoreDeliveryAreaRepository;
import com.sparta.dingdong.domain.store.repository.StoreRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final StoreRepository storeRepository;
    private final StoreDeliveryAreaRepository storeDeliveryAreaRepository;
    private final PaymentTransactionService paymentTransactionService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserService userService,
            CartService cartService,
            StoreRepository storeRepository,
            StoreDeliveryAreaRepository storeDeliveryAreaRepository,
            PaymentTransactionService paymentTransactionService
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
        this.storeRepository = storeRepository;
        this.storeDeliveryAreaRepository = storeDeliveryAreaRepository;
        this.paymentTransactionService = paymentTransactionService;
    }

    @Transactional
    public OrderResponseDto createOrder(UserAuth userAuth, CreateOrderRequestDto request) {
        User user = userService.findByUser(userAuth);

        Cart cart = cartService.findByCart(request.getCartId());

        Store store = storeRepository.findById(cart.getStore().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));

        String userDongId = user.getAddressList().stream()
                .findFirst()
                .map(address -> address.getDong().getId()) // Dong의 id가 String
                .orElseThrow(() -> new IllegalStateException("배송지 정보가 없습니다."));

        // 매장의 배달 가능 dongId 목록만 조회
        List<String> deliveryDongIds = storeDeliveryAreaRepository.findDongIdsByStoreId(store.getId());

        System.out.println("고객 주소 dongId = " + userDongId);
        System.out.println("매장 배달 가능 dongId 목록 = " + deliveryDongIds);
        System.out.println("store.getId() = " + store.getId());
        System.out.println("storeDeliveryAreaRepository 결과 = " + deliveryDongIds);

        if (!deliveryDongIds.contains(userDongId)) {
            throw new IllegalStateException("배달불가지역입니다.");
        }

        BigInteger totalPrice = cart.getItems().stream()
                .map(item -> item.getMenuItem().getPrice()
                        .multiply(BigInteger.valueOf(item.getQuantity())))
                .reduce(BigInteger.ZERO, BigInteger::add);

        Order order = Order.create(
                user,
                store,
                totalPrice,
                request.getDeliveryAddress(),
                OrderStatus.PENDING
        );
/*
		for (var cartItem : cart.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setMenuItem(cartItem.getMenuItem());
			orderItem.setUnitPrice(cartItem.getMenuItem().getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			order.addOrderItem(orderItem); // 양방향 연관관계 설정
		}
*/
        orderRepository.save(order);

        cartService.deleteCart(cart);

        return OrderResponseDto.from(order);
    }

    public OrderDetailResponseDto getOrderDetail(UserAuth userAuth, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(userAuth.getId())) {
            throw new IllegalStateException("본인의 주문만 조회할 수 있습니다.");
        }

        return OrderDetailResponseDto.from(order);
    }

    public OrderListResponseDto getOrderList(UserAuth userAuth) {
        List<Order> orders = orderRepository.findAllByUserId(userAuth.getId());
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(OrderResponseDto::from)
                .collect(Collectors.toList());
        return new OrderListResponseDto(orderDtos);
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, UpdateOrderStatusRequestDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.changeStatus(request.getNewStatus());
    }

    @Transactional
    public Order cancelOrder(UserAuth userAuth, UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 본인 주문인지 확인
        if (!order.getUser().getId().equals(userAuth.getId())) {
            throw new IllegalStateException("본인의 주문만 취소할 수 있습니다.");
        }
        //Pending 취소
        if (order.getStatus() == OrderStatus.PENDING) {
            order.cancel(reason);
            //Request 취소
        } else if (order.getStatus() == OrderStatus.REQUESTED) {
            paymentTransactionService.refundPayment(order, "사용자 주문 취소: " + reason);
            order.cancel(reason);

        } else {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        orderRepository.save(order);
        return order;
    }

    public Order findByOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당하는 Order가 없습니다."));
    }

    @Override
    public List<Order> findByOrders(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderListResponseDto getOrdersByStore(UserAuth userAuth, UUID storeId, String status) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));

        if (!store.getOwner().getId().equals(userAuth.getId())) {
            throw new IllegalStateException("본인 매장만 조회할 수 있습니다.");
        }

        List<Order> orders;
        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findAllByStoreIdAndStoreOwnerIdAndStatus(storeId, userAuth.getId(), orderStatus);
        } else {
            orders = orderRepository.findAllByStoreIdAndStoreOwnerId(storeId, userAuth.getId());
        }

        return new OrderListResponseDto(
                orders.stream().map(OrderResponseDto::from).toList()
        );
    }

    @Transactional(readOnly = true)
    public OrderListResponseDto getAllOrdersByOwner(UserAuth userAuth, String status) {
        List<Order> orders;
        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findAllByStoreOwnerIdAndStatus(userAuth.getId(), orderStatus);
        } else {
            orders = orderRepository.findAllByStoreOwnerId(userAuth.getId());
        }

        return new OrderListResponseDto(
                orders.stream().map(OrderResponseDto::from).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrdersByAdmin(UUID storeId, UUID orderId, String status, Pageable pageable) {
        Page<Order> orders;

        if (orderId != null) {
            orders = orderRepository.findAllByIdEquals(orderId, pageable);
        } else if (storeId != null && status != null) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findAllByStoreIdAndStatus(storeId, orderStatus, pageable);
        } else if (storeId != null) {
            orders = orderRepository.findAllByStoreId(storeId, pageable);
        } else if (status != null) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findAllByStatus(orderStatus, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(OrderResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetailByAdmin(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        return OrderDetailResponseDto.from(order);
    }

}
