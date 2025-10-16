package com.sparta.dingdong.domain.order.exception;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.exception.CommonErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderNotFoundException(OrderNotFoundException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_NOT_FOUND.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_NOT_FOUND));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleInvalidOrderStatusException(InvalidOrderStatusException ex) {
        return ResponseEntity.status(CommonErrorCode.INVALID_ORDER_STATUS.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.INVALID_ORDER_STATUS));
    }

    @ExceptionHandler(OrderAlreadyCanceledException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderAlreadyCanceledException(OrderAlreadyCanceledException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_ALREADY_CANCELED.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_ALREADY_CANCELED));
    }

    @ExceptionHandler(OrderAlreadyCompletedException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderAlreadyCompletedException(OrderAlreadyCompletedException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_ALREADY_COMPLETED.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_ALREADY_COMPLETED));
    }

    @ExceptionHandler(OrderPermissionDeniedException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderPermissionDeniedException(OrderPermissionDeniedException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_NOT_OWNED_BY_USER.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_NOT_OWNED_BY_USER));
    }

    @ExceptionHandler(OrderDeliveryUnavailableException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderDeliveryUnavailableException(OrderDeliveryUnavailableException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_DELIVERY_UNAVAILABLE.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_DELIVERY_UNAVAILABLE));
    }

    @ExceptionHandler(OrderBelowMinPriceException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderBelowMinPriceException(OrderBelowMinPriceException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_BELOW_MIN_PRICE.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_BELOW_MIN_PRICE, ex.getMessage()));
    }

    @ExceptionHandler(OrderCancelTimeExceededException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOrderCancelTimeExceededException(OrderCancelTimeExceededException ex) {
        return ResponseEntity.status(CommonErrorCode.ORDER_CANCEL_TIME_EXCEEDED.getStatus())
                .body(BaseResponseDto.error(CommonErrorCode.ORDER_CANCEL_TIME_EXCEEDED, ex.getMessage()));
    }
}
