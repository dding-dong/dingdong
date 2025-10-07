package com.sparta.dingdong.domain.menu.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class MenuExceptionHandler {
	@ExceptionHandler(MenuItemNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuItemNotFoundException(MenuItemNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_ITEM_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_ITEM_NOT_FOUND));
	}

	@ExceptionHandler(HiddenMenuAccessDeniedException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleHiddenMenuAccessDeniedException(
		HiddenMenuAccessDeniedException ex) {
		return ResponseEntity.status(CommonErrorCode.HIDDEN_MENU_ACCESS_DENIED.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.HIDDEN_MENU_ACCESS_DENIED));
	}

	@ExceptionHandler(DeletedStoreMenuAccessException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleDeletedStoreMenuAccessException(
		DeletedStoreMenuAccessException ex) {
		return ResponseEntity.status(CommonErrorCode.DELETED_STORE_MENU_ACCESS.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.DELETED_STORE_MENU_ACCESS));
	}

	@ExceptionHandler(MenuItemSoldOutException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuItemSolddOutException(MenuItemSoldOutException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_ITEM_SOLD_OUT.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_ITEM_SOLD_OUT));
	}
}
