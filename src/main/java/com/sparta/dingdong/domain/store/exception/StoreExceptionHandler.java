package com.sparta.dingdong.domain.store.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class StoreExceptionHandler {
	@ExceptionHandler(StoreNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleStoreNotFoundException(StoreNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.STORE_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.STORE_NOT_FOUND));
	}

	@ExceptionHandler(DeliveryAreaNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleDeliveryAreaNotFoundException(
		DeliveryAreaNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.DELIVERY_AREA_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.DELIVERY_AREA_NOT_FOUND));
	}

	@ExceptionHandler(NotStoreOwnerException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleNotStoreOwnerException(NotStoreOwnerException ex) {
		return ResponseEntity.status(CommonErrorCode.NOT_STOREOWNER.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.NOT_STOREOWNER));
	}

	@ExceptionHandler(DeletedStoreNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleDeletedStoreNotFoundException(DeletedStoreNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.DELETED_STORE_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.DELETED_STORE_NOT_FOUND));
	}
}
