package com.sparta.dingdong.domain.order.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.dto.request.CreateOrderRequestDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderDetailResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderListResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderResponseDto;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "주문 API", description = "주문 관련 API 입니다.")
public class OrderControllerV1 {

	private final OrderService orderService;

	@Operation(summary = "주문 생성 API", description = "고객이 장바구니를 기반으로 주문을 생성합니다.")
	@PostMapping
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	public ResponseEntity<BaseResponseDto<OrderResponseDto>> createOrder(
		@AuthenticationPrincipal UserAuth userAuth,
		@Valid @RequestBody CreateOrderRequestDto request
	) {
		OrderResponseDto response = orderService.createOrder(userAuth, request);
		return ResponseEntity.ok(BaseResponseDto.success("주문이 생성되었습니다.", response));
	}

	@Operation(summary = "주문 상세 조회 API", description = "사용자가 자신의 주문 상세를 조회합니다.")
	@GetMapping("/{orderId}")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	public ResponseEntity<BaseResponseDto<OrderDetailResponseDto>> getOrderDetail(
		@AuthenticationPrincipal UserAuth userAuth,
		@PathVariable UUID orderId
	) {
		OrderDetailResponseDto response = orderService.getOrderDetail(userAuth, orderId);
		return ResponseEntity.ok(BaseResponseDto.success("주문 상세 조회입니다.", response));
	}

	@Operation(summary = "사용자 주문 목록 조회 API", description = "사용자의 모든 주문 내역을 조회합니다.")
	@GetMapping
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	public ResponseEntity<BaseResponseDto<OrderListResponseDto>> getOrderList(
		@AuthenticationPrincipal UserAuth userAuth
	) {
		OrderListResponseDto response = orderService.getOrderList(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("주문 목록 조회입니다.", response));
	}

	@Operation(summary = "주문 상태 변경 API", description = "점주(OWNER), 매니저(MANAGER), 마스터(MASTER)가 주문 상태를 변경합니다.")
	@PatchMapping("/{orderId}/status")
	@PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_MANAGER', 'ROLE_MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> updateOrderStatus(
		@PathVariable UUID orderId,
		@Valid @RequestBody UpdateOrderStatusRequestDto request
	) {
		orderService.updateOrderStatus(orderId, request);
		return ResponseEntity.ok(BaseResponseDto.success("주문 상태가 변경되었습니다."));
	}

	@Operation(summary = "고객 주문 취소 API", description = "고객이 자신의 주문을 취소합니다. (PENDING 또는 REQUESTED 상태에서만 가능)")
	@PatchMapping("/{orderId}/cancel")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	public ResponseEntity<BaseResponseDto<Map<String, Object>>> cancelOrder(
		@AuthenticationPrincipal UserAuth userAuth,
		@PathVariable UUID orderId,
		@RequestParam(required = false) String reason
	) {
		Order canceledOrder = orderService.cancelOrder(userAuth, orderId, reason);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("orderId", canceledOrder.getId());
		responseData.put("status", canceledOrder.getStatus().name());
		responseData.put("canceledAt", canceledOrder.getCanceledAt());
		responseData.put("cancelReason", canceledOrder.getCancelReason());

		return ResponseEntity.ok(BaseResponseDto.success("주문이 취소되었습니다.", responseData));
	}
}