package com.sparta.dingdong.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.review.exception.NotReviewOwnerException;
import com.sparta.dingdong.domain.review.exception.OrderAlreadyReviewedException;
import com.sparta.dingdong.domain.review.exception.ReviewAlreadyRepliedException;
import com.sparta.dingdong.domain.review.exception.ReviewNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ReviewNotFoundException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleReviewNotFoundException(ReviewNotFoundException ex) {
		return ResponseEntity
			.status(CommonErrorCode.REVIEW_NOT_FOUND.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.REVIEW_NOT_FOUND));
	}
	
	@ExceptionHandler(NotReviewOwnerException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleNotReviewOwnerException(NotReviewOwnerException ex) {
		return ResponseEntity
			.status(CommonErrorCode.NOT_REVIEW_OWNER.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.NOT_REVIEW_OWNER));
	}

	@ExceptionHandler(OrderAlreadyReviewedException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleOrderAlreadyReviewedException(OrderAlreadyReviewedException ex) {
		return ResponseEntity
			.status(CommonErrorCode.ORDER_ALREADY_REVIEWED.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.ORDER_ALREADY_REVIEWED));
	}

	@ExceptionHandler(ReviewAlreadyRepliedException.class)
	public ResponseEntity<BaseResponseDto<Void>> handleReviewAlreadyRepliedException(ReviewAlreadyRepliedException ex) {
		return ResponseEntity
			.status(CommonErrorCode.REVIEW_ALREADY_REPLIED.getStatus())
			.body(BaseResponseDto.error(CommonErrorCode.REVIEW_ALREADY_REPLIED));
	}
}
