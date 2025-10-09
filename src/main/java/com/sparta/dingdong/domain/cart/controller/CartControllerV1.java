package com.sparta.dingdong.domain.cart.controller;

import java.util.UUID;

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
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.cart.dto.request.AddCartItemRequestDto;
import com.sparta.dingdong.domain.cart.dto.request.UpdateCartItemRequestDto;
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
		CartResponseDto dto = cartService.getCart(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("장바구니 조회입니다.", dto));
	}

	@PostMapping("/items")
	public ResponseEntity<BaseResponseDto<CartResponseDto>> addItem(
		@Valid @RequestBody AddCartItemRequestDto req,
		@RequestParam(required = false, defaultValue = "false") boolean replace,
		@AuthenticationPrincipal UserAuth userAuth) {
		CartResponseDto dto = cartService.addItem(userAuth, req, replace);
		return ResponseEntity.ok(BaseResponseDto.success("menuItem이 추가되었습니다.", dto));
	}

	@PutMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<CartResponseDto>> updateItem(
		@PathVariable UUID menuItemId,
		@Valid @RequestBody UpdateCartItemRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		CartResponseDto dto = cartService.updateItemQuantity(userAuth, menuItemId, req.getQuantity());
		return ResponseEntity.ok(BaseResponseDto.success("수량이 변경되었습니다.", dto));
	}

	@DeleteMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<Void>> deleteItem(
		@PathVariable UUID menuItemId,
		@AuthenticationPrincipal UserAuth userAuth) {
		cartService.removeItem(userAuth, menuItemId); // 명확한 remove 메서드 권장
		return ResponseEntity.ok(BaseResponseDto.success("menuItem 삭제되었습니다."));
	}

	@DeleteMapping
	public ResponseEntity<BaseResponseDto<Void>> clearCart(@AuthenticationPrincipal UserAuth userAuth) {
		cartService.clearCart(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("장바구니 삭제되었습니다."));
	}
}
