package com.sparta.dingdong.domain.order.controller;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "주문 상태 변경 API", description = "관리자(MANAGER, MASTER)가 주문 상태를 변경합니다.")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponseDto<Void>> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto request
    ) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(BaseResponseDto.success("주문 상태가 변경되었습니다."));
    }

    // ✅ 추후 확장 예정:
    // @GetMapping -> 전체 주문 조회
    // @GetMapping("/{orderId}") -> 주문 상세
    // @GetMapping("/{orderId}/history") -> 주문 변경 히스토리
}

