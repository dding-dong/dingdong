package com.sparta.dingdong.domain.cart.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class CartExceptionHandler {
	@ExceptionHandler(CartNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleCartNotFoundException(CartNotFoundException ex) {
		return ResponseEntity
			.status(CommonErrorCode.CART_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.CART_NOT_FOUND));
	}

	@ExceptionHandler(CartItemNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleCartItemNotFoundException(CartItemNotFoundException ex) {
		return ResponseEntity
			.status(CommonErrorCode.CART_ITEM_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.CART_ITEM_NOT_FOUND));
	}

	@ExceptionHandler(CartStoreConflictException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleCartStoreConflictException(CartStoreConflictException ex) {
		return ResponseEntity
			.status(CommonErrorCode.CART_STORE_CONFLICT.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.CART_STORE_CONFLICT));
	}

	@ExceptionHandler(InvalidCartQuantityException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleInvalidCartQuantityException(InvalidCartQuantityException ex) {
		return ResponseEntity
			.status(CommonErrorCode.INVALID_CART_QUANTITY.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.INVALID_CART_QUANTITY));
	}

}
