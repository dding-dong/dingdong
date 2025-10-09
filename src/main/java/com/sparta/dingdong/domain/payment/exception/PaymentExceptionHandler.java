package com.sparta.dingdong.domain.payment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class PaymentExceptionHandler {

	@ExceptionHandler(PaymentAlreadyExistsException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentAlreadyExistsException(PaymentAlreadyExistsException ex) {
		return ResponseEntity
			.status(CommonErrorCode.PAYMENT_ALREADY_EXISTS.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_ALREADY_EXISTS));
	}

	@ExceptionHandler(PaymentAmountMismatchException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentAmountMismatchException(
		PaymentAmountMismatchException ex) {
		return ResponseEntity
			.status(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH));
	}

	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentNotFoundException(PaymentNotFoundException ex) {
		return ResponseEntity
			.status(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH));
	}

}
