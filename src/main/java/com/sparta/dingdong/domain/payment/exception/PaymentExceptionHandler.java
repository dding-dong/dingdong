package com.sparta.dingdong.domain.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;

@RestControllerAdvice
public class PaymentExceptionHandler {

	@ExceptionHandler(PaymentAlreadyExistsException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentAlreadyExistsException(PaymentAlreadyExistsException ex) {
		return ResponseEntity.status(CommonErrorCode.PAYMENT_ALREADY_EXISTS.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_ALREADY_EXISTS));
	}

	@ExceptionHandler(PaymentAmountMismatchException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentAmountMismatchException(
		PaymentAmountMismatchException ex) {
		return ResponseEntity.status(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH));
	}

	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentNotFoundException(PaymentNotFoundException ex) {
		return ResponseEntity.status(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_AMOUNT_MISMATCH));
	}

	@ExceptionHandler(PaymentStatusNoPendingException.class)
	public ResponseEntity<BaseResponseDto<Void>> handlePaymentStatusNoPendingException(
		PaymentStatusNoPendingException ex) {
		return ResponseEntity.status(CommonErrorCode.PAYMENT_STATUS_NO_PEDNIND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.PAYMENT_STATUS_NO_PEDNIND));
	}

	@ExceptionHandler(TossConfirmFailedException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleTossConfirmFailedException(TossConfirmFailedException ex) {
		return ResponseEntity.status(CommonErrorCode.TOSS_CONFIRM_FAILED.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.TOSS_CONFIRM_FAILED));
	}

	@ExceptionHandler(TossCancelFailedException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleTossCancelFailedException(TossCancelFailedException ex) {
		return ResponseEntity.status(CommonErrorCode.TOSS_CANCEL_FAILED.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.TOSS_CANCEL_FAILED));
	}

	@ExceptionHandler(TossConfirmPageException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleTossConfirmPageException(TossConfirmPageException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_GATEWAY)
			.body(BaseResponseDto.error(ex.getErrorCode(), ex.getErrorMessage()));
	}
}
