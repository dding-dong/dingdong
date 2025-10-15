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
@RequestMapping("/v1/owners/orders")
@Tag(name = "Owner 주문 API", description = "점주(OWNER)용 주문 관련 API입니다.")
@PreAuthorize("hasRole('ROLE_OWNER')")
public class OwnerOrderControllerV1 {

    private final OrderService orderService;

    @Operation(summary = "주문 상태 변경 API", description = "점주가 주문 상태를 변경합니다.")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponseDto<Void>> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto request
    ) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(BaseResponseDto.success("주문 상태가 변경되었습니다."));
    }

    // ✅ 추후 추가 예정:
    // @GetMapping("/stores/{storeId}")
    // -> orderService.getOrdersByStoreId(storeId)
}
