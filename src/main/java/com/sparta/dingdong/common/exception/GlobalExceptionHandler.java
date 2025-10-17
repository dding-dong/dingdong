package com.sparta.dingdong.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {
		return ResponseEntity.status(CommonErrorCode.INVALID_JSON_FORMAT.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.INVALID_JSON_FORMAT, ex.getBindingResult()));
	}

}
