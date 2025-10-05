package com.sparta.dingdong.domain.cart.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.request.UpdateCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.response.CartConflictResponseDto;
import com.sparta.dingdong.domain.cart.dto.response.CartResponseDto;
import com.sparta.dingdong.domain.cart.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartControllerV1 {

	private final CartService cartService;

	@GetMapping
	public ResponseEntity<BaseResponseDto<?>> getCart(@AuthenticationPrincipal UserAuth userAuth) {
		if (userAuth == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(BaseResponseDto.error(CommonErrorCode.UNAUTHORIZED));
		}

		Long userId = userAuth.getId();
		CartResponseDto dto = cartService.getCart(userId);
		return ResponseEntity.ok(BaseResponseDto.success("카트 조회 성공", dto));
	}

	@PostMapping("/items")
	public ResponseEntity<BaseResponseDto<?>> addItem(@Valid @RequestBody AddCartItemRequestDto req,
		@RequestParam(required = false, defaultValue = "false") boolean replace,
		@AuthenticationPrincipal UserAuth userAuth) {
		Long userId = userAuth.getId();
		Object result = cartService.addItem(userId, req, replace);
		if (result instanceof CartConflictResponseDto conflictDto) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(BaseResponseDto.success("장바구니 충돌 발생", conflictDto));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(BaseResponseDto.success("아이템 추가 성공", result));
	}

	@PutMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<CartResponseDto>> updateItem(@PathVariable UUID menuItemId,
		@Valid @RequestBody UpdateCartItemRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		Long userId = userAuth.getId();
		CartResponseDto dto = cartService.updateItemQuantity(userId, menuItemId, req.getQuantity());
		return ResponseEntity.ok(BaseResponseDto.success("수량 변경 성공", dto));
	}

	@DeleteMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<Void>> deleteItem(@PathVariable UUID menuItemId,
		@AuthenticationPrincipal UserAuth userAuth) {
		Long userId = userAuth.getId();
		cartService.removeItem(userId, menuItemId); // 명확한 remove 메서드 권장
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<BaseResponseDto<Void>> clearCart(@AuthenticationPrincipal UserAuth userAuth) {
		Long userId = userAuth.getId();
		cartService.clearCart(userId);
		return ResponseEntity.noContent().build();
	}
}
