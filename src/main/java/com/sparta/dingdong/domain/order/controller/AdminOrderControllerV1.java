package com.sparta.dingdong.domain.order.controller;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderDetailResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderResponseDto;
import com.sparta.dingdong.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/orders")
@Tag(name = "Admin 주문 API", description = "관리자(MANAGER, MASTER)용 주문 관련 API입니다.")
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MASTER')")
public class AdminOrderControllerV1 {

    private final OrderService orderService;

    @Operation(summary = "주문 목록 조회 API", description = "storeId, orderId, status로 필터링된 주문 목록을 페이징으로 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<OrderResponseDto>>> getAllOrders(
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<OrderResponseDto> response = orderService.getAllOrdersByAdmin(storeId, orderId, status, pageable);
        return ResponseEntity.ok(BaseResponseDto.success("주문 목록 조회 성공", response));
    }


    @Operation(summary = "주문 상세 조회 API", description = "관리자가 특정 주문의 상세정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponseDto<OrderDetailResponseDto>> getOrderDetail(@PathVariable UUID orderId) {
        OrderDetailResponseDto response = orderService.getOrderDetailByAdmin(orderId);
        return ResponseEntity.ok(BaseResponseDto.success("주문 상세 조회 성공", response));
    }

    @Operation(summary = "주문 상태 변경 API", description = "관리자(MANAGER, MASTER)가 주문 상태를 변경합니다.")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponseDto<Void>> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto request
    ) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(BaseResponseDto.success("주문 상태가 변경되었습니다."));
    }

}

