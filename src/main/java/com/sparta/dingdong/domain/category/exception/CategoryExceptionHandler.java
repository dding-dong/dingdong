package com.sparta.dingdong.domain.category.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;
import com.sparta.dingdong.domain.category.exception.menucategory.DeletedMenuCategoryNotFoundException;
import com.sparta.dingdong.domain.category.exception.menucategory.MenuCategoryItemNotFoundException;
import com.sparta.dingdong.domain.category.exception.menucategory.MenuCategoryItemOrderConflictException;
import com.sparta.dingdong.domain.category.exception.menucategory.MenuCategoryNotFoundException;
import com.sparta.dingdong.domain.category.exception.menucategory.MenuCategorySortConflictException;
import com.sparta.dingdong.domain.category.exception.storecategory.StoreCategoryAlreadyActiveException;
import com.sparta.dingdong.domain.category.exception.storecategory.StoreCategoryNotFoundException;

@RestControllerAdvice
public class CategoryExceptionHandler {
	@ExceptionHandler(StoreCategoryNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleStoreCategoryNotFoundException(
		StoreCategoryNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.STORE_CATEGORY_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.STORE_CATEGORY_NOT_FOUND));
	}

	@ExceptionHandler(StoreCategoryAlreadyActiveException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleStoreCategoryAlreadyActiveException(
		StoreCategoryAlreadyActiveException ex) {
		return ResponseEntity.status(CommonErrorCode.STORE_CATEGORY_ALREADY_ACTIVE.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.STORE_CATEGORY_ALREADY_ACTIVE));
	}

	@ExceptionHandler(MenuCategoryNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuCategoryNotFoundException(MenuCategoryNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_CATEGORY_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_CATEGORY_NOT_FOUND));
	}

	@ExceptionHandler(MenuCategorySortConflictException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuCategorySortConflictException(
		MenuCategorySortConflictException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_CATEGORY_SORT_CONFLICT.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_CATEGORY_SORT_CONFLICT));
	}

	@ExceptionHandler(DeletedMenuCategoryNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleDeletedMenuCategoryNotFoundException(
		DeletedMenuCategoryNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.DELETED_MENU_CATEGORY_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.DELETED_MENU_CATEGORY_NOT_FOUND));
	}

	@ExceptionHandler(MenuCategoryItemNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuCategoryItemNotFoundException(
		MenuCategoryItemNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_CATEGORY_ITEM_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_CATEGORY_ITEM_NOT_FOUND));
	}

	@ExceptionHandler(MenuCategoryItemOrderConflictException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMenuCategoryItemOrderConflictException(
		MenuCategoryItemOrderConflictException ex) {
		return ResponseEntity.status(CommonErrorCode.MENU_CATEGORY_ITEM_ORDER_CONFLICT.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.MENU_CATEGORY_ITEM_ORDER_CONFLICT));
	}
}
