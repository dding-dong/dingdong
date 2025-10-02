package com.sparta.dingdong.common.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.dingdong.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 Json응답에서 제외, 성공한 요청에는 errors가 필요 없으니 JSON에서 아예 빠지도록 설정
public class BaseResponseDto<T> {

	private String status; // "SUCCESS" 또는 "FAIL"
	private String code;   // S200, U001 등
	private String message;
	private T data;        // 성공 시 응답 데이터
	private List<ErrorResponseDto.FieldError> errors; // 실패 시 필드 오류 목록

	public static <T> BaseResponseDto<T> success(String message, T data) {
		return BaseResponseDto.<T>builder()
			.status("SUCCESS")
			.code("200")
			.message(message)
			.data(data)
			.build();
	}

	public static BaseResponseDto<Void> success(String message) {
		return success(message, null);
	}

	public static BaseResponseDto<Void> error(ErrorCode errorCode) {
		return BaseResponseDto.<Void>builder()
			.status("FAIL")
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.errors(Collections.emptyList())
			.build();
	}

	public static BaseResponseDto<Void> error(ErrorCode errorCode, BindingResult bindingResult) {
		return BaseResponseDto.<Void>builder()
			.status("FAIL")
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.errors(ErrorResponseDto.FieldError.of(bindingResult))
			.build();
	}
}
