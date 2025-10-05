package com.sparta.dingdong.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "000", "서버 내부 오류입니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "001", "입력값이 올바르지 않습니다."),
	MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "002", "필수 파라미터가 누락되었습니다."),
	INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "003", "요청 형식이 올바르지 않습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "004", "허용되지 않은 HTTP 메서드입니다."),
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "005", "지원하지 않는 미디어 타입입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "006", "인증이 필요합니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "007", "접근 권한이 없습니다."),

	REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "800", "해당 리뷰를 찾을 수 없습니다."),
	NOT_REVIEW_OWNER(HttpStatus.BAD_REQUEST, "801", "리뷰 작성자가 아닙니다."),
	ORDER_ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, "802", "이미 해당 주문에 대한 리뷰가 존재합니다."),
	REVIEW_ALREADY_REPLIED(HttpStatus.BAD_REQUEST, "803", "이미 해당 리뷰에 대한 답글이 존재합니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
