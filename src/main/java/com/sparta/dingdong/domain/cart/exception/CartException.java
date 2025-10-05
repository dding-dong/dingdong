package com.sparta.dingdong.domain.cart.exception;

import org.springframework.http.HttpStatus;

import com.sparta.dingdong.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartException implements ErrorCode {
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "장바구니를 찾을 수 없습니다."),
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "장바구니 상품을 찾을 수 없습니다."),
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "존재하지 않는 메뉴입니다."),
	MENU_SOLD_OUT(HttpStatus.BAD_REQUEST, "C004", "품절된 메뉴입니다."),
	CART_OTHER_STORE(HttpStatus.BAD_REQUEST, "C005", "다른 가게의 상품은 장바구니에 담을 수 없습니다."),
	CART_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "C006", "이미 장바구니에 담긴 상품입니다."),
	INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "C007", "수량은 1 이상이어야 합니다."),
	REMOVE_ITEM_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C008", "상품 삭제에 실패했습니다."),
	CLEAR_CART_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C009", "장바구니 비우기에 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
