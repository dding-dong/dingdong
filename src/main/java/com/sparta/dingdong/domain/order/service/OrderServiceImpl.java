package com.sparta.dingdong.domain.order.service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.entity.Cart;
import com.sparta.dingdong.domain.cart.repository.CartRepository;
import com.sparta.dingdong.domain.order.dto.request.CreateOrderRequestDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderDetailResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderListResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderResponseDto;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;
import com.sparta.dingdong.domain.order.repository.OrderRepository;
import com.sparta.dingdong.domain.payment.service.PaymentService;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.repository.UserRepository;

@Service
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CartRepository cartRepository;

	private final PaymentService paymentService;

	public OrderServiceImpl(
		OrderRepository orderRepository,
		UserRepository userRepository,
		CartRepository cartRepository,
		@Lazy PaymentService paymentService
	) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.paymentService = paymentService;
	}

	@Transactional
	public OrderResponseDto createOrder(UserAuth userAuth, CreateOrderRequestDto request) {
		User user = userRepository.findById(userAuth.getId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Cart cart = cartRepository.findById(request.getCartId())
			.orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

		Store store = cart.getStore();

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

		orderRepository.save(order);

		cartRepository.delete(cart);

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
			paymentService.refundPayment(order, "사용자 주문 취소: " + reason);
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
}
