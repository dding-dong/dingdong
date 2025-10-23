package com.sparta.dingdong.domain.cart.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
@Tag(name = "고객 장바구니 API", description = "고객 장바구니 API 입니다.")
public class CartControllerV1 {

	private final CartService cartService;

	@Operation(summary = "고객 장바구니 조회 API", description = "고객이 장바구니를 조회합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping
	public ResponseEntity<BaseResponseDto<?>> getCart(
		@AuthenticationPrincipal UserAuth userAuth) {
		CartResponseDto dto = cartService.getCart(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("장바구니를 조회합니다.", dto));
	}

	@Operation(summary = "장바구니에 메뉴 추가 API", description = "고객이 장바구니에 메뉴를 추가합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PostMapping("/items")
	public ResponseEntity<BaseResponseDto<CartResponseDto>> addItem(
		@Valid @RequestBody AddCartItemRequestDto req,
		@Parameter(description = "장바구니 대체 결정 여부") @RequestParam(required = false, defaultValue = "false") boolean replace,
		@AuthenticationPrincipal UserAuth userAuth) {
		CartResponseDto dto = cartService.addItem(userAuth, req, replace);
		return ResponseEntity.ok(BaseResponseDto.success("메뉴가 장바구니에 추가되었습니다.", dto));
	}

	@Operation(summary = "장바구니에 담긴 메뉴 수량 변경 API", description = "장바구니에 담긴 메뉴 수량을 변경합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PutMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<CartResponseDto>> updateItem(
		@Parameter(description = "메뉴 아이템 UUID") @PathVariable UUID menuItemId,
		@Valid @RequestBody UpdateCartItemRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		CartResponseDto dto = cartService.updateItemQuantity(userAuth, menuItemId, req.getQuantity());
		return ResponseEntity.ok(BaseResponseDto.success("수량이 변경되었습니다.", dto));
	}

	@Operation(summary = "장바구니에 담긴 메뉴 삭제 API", description = "장바구니에 담긴 메뉴를 삭제합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@DeleteMapping("/items/{menuItemId}")
	public ResponseEntity<BaseResponseDto<Void>> deleteMenuItem(
		@Parameter(description = "메뉴 아이템 UUID") @PathVariable UUID menuItemId,
		@AuthenticationPrincipal UserAuth userAuth) {
		cartService.deleteItem(userAuth, menuItemId); // 명확한 remove 메서드 권장
		return ResponseEntity.ok(BaseResponseDto.success("장바구니에 담긴 메뉴가 삭제되었습니다."));
	}

	@Operation(summary = "장바구니 삭제 API", description = "장바구니를 삭제합니다.")
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@DeleteMapping
	public ResponseEntity<BaseResponseDto<Void>> deleteCart(
		@AuthenticationPrincipal UserAuth userAuth) {
		cartService.deleteCart(userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("장바구니가 삭제되었습니다."));
	}
}
