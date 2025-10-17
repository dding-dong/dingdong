package com.sparta.dingdong.domain.AI.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class AIExceptionHandler {

	@ExceptionHandler(GoogleApiKeyMissingException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleGoogleApiKeyMissingException(GoogleApiKeyMissingException ex) {
		return ResponseEntity.status(CommonErrorCode.GOOGLE_API_KEY_MISSING.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.GOOGLE_API_KEY_MISSING));
	}

	@ExceptionHandler(GoogleApiCallException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleGoogleApiCallException(GoogleApiCallException ex) {
		return ResponseEntity.status(CommonErrorCode.GOOGLE_API_CALL_FAIL.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.GOOGLE_API_CALL_FAIL));
	}
}
