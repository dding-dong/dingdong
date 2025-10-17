package com.sparta.dingdong.domain.order.controller;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderListResponseDto;
import com.sparta.dingdong.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners/orders")
@Tag(name = "Owner 주문 API", description = "점주(OWNER)용 주문 관련 API입니다.")
@PreAuthorize("hasRole('ROLE_OWNER')")
public class OwnerOrderControllerV1 {

    private final OrderService orderService;

    @Operation(summary = "점주의 전체 주문 조회 API", description = "로그인한 점주가 담당하는 모든 매장의 주문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponseDto<OrderListResponseDto>> getAllOrdersByOwner(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestParam(required = false) String status
    ) {
        OrderListResponseDto response = orderService.getAllOrdersByOwner(userAuth, status);
        return ResponseEntity.ok(BaseResponseDto.success("점주 전체 주문 목록 조회 성공", response));
    }

    @Operation(summary = "매장 주문 목록 조회 API", description = "특정 매장의 주문 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}/orders")
    public ResponseEntity<BaseResponseDto<OrderListResponseDto>> getOrdersByStore(
            @AuthenticationPrincipal UserAuth userAuth,
            @PathVariable UUID storeId,
            @RequestParam(required = false) String status
    ) {
        OrderListResponseDto response = orderService.getOrdersByStore(userAuth, storeId, status);
        return ResponseEntity.ok(BaseResponseDto.success("매장 주문 목록 조회 성공", response));
    }

    @Operation(summary = "주문 상태 변경 API", description = "점주가 주문 상태를 변경합니다.")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponseDto<Void>> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto request
    ) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(BaseResponseDto.success("주문 상태가 변경되었습니다."));
    }
}
