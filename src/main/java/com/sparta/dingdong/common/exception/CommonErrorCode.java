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
	REVIEW_ALREADY_REPLIED(HttpStatus.BAD_REQUEST, "803", "이미 해당 리뷰에 대한 답글이 존재합니다."),
	REVIEW_REPLY_NOT_FOUND(HttpStatus.BAD_REQUEST, "804", "해당 리뷰의 댓글을 찾을 수 없습니다."),
	NOT_REVIEW_REPLY_OWNER(HttpStatus.BAD_REQUEST, "805", "리뷰 답글의 작성자가 아닙니다."),
	NOT_STORE_OWNER(HttpStatus.BAD_REQUEST, "806", "가게 사장님만 접근 가능합니다."),

	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "1001", "장바구니를 찾을 수 없습니다."),
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "1002", "장바구니에 담은 해당 메뉴를 찾을 수 없습니다."),
	CART_STORE_CONFLICT(HttpStatus.CONFLICT, "1003", "장바구니는 하나의 가게만 담을 수 있습니다."),
	INVALID_CART_QUANTITY(HttpStatus.BAD_REQUEST, "1004", "수량은 1 이상이어야 합니다."),

	STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "2000", "해당 가게를 찾을 수 없습니다."),
	DELETED_STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "2001", "삭제된 가게를 찾을 수 없습니다."),
	DELIVERY_AREA_NOT_FOUND(HttpStatus.BAD_REQUEST, "2002", "해당 배달가능지역을 찾을 수 없습니다."),
	//	NOT_STOREOWNER(HttpStatus.FORBIDDEN, "2003", "해당 가게의 사장님만 접근 가능합니다."),

	MENU_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "2100", "해당 메뉴 아이템을 찾을 수 없습니다."),
	HIDDEN_MENU_ACCESS_DENIED(HttpStatus.FORBIDDEN, "2101", "숨김 메뉴 아이템 조회 권한이 없습니다."),
	DELETED_STORE_MENU_ACCESS(HttpStatus.FORBIDDEN, "2102", "삭제된 가게의 메뉴 아이템에 접근 권한이 없습니다."),
	MENU_ITEM_SOLD_OUT(HttpStatus.BAD_REQUEST, "2103", "해당 메뉴는 품절입니다."),

	STORE_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "2200", "해당 가게 카테고리를 찾을 수 없습니다."),
	STORE_CATEGORY_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "2201", "이미 활성화된 가게 카테고리입니다."),
	MENU_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "2300", "해당 메뉴 카테고리를 찾을 수 없습니다."),
	DELETED_MENU_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "2301", "삭제된 메뉴 카테고리를 찾을 수 없습니다."),
	MENU_CATEGORY_SORT_CONFLICT(HttpStatus.BAD_REQUEST, "2302", "해당 정렬 순서를 가진 메뉴 카테고리가 이미 존재합니다."),
	MENU_CATEGORY_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "2400", "해당 메뉴 카테고리의 메뉴 아이템이 존재하지 않습니다."),
	MENU_CATEGORY_ITEM_ORDER_CONFLICT(HttpStatus.BAD_REQUEST, "2401", "해당 카테고리에서 이미 같은 순서(orderNo)의 메뉴 아이템이 존재합니다."),

	GOOGLE_API_KEY_MISSING(HttpStatus.BAD_REQUEST, "3000", "Google API 키가 설정되어 있지 않습니다."),
	GOOGLE_API_CALL_FAIL(HttpStatus.BAD_REQUEST, "3001", "Google AI API 호출 실패"),

	PAYMENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "4000", "이미 결제 진행 중인 주문입니다."),
	PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "4001", "결제 가격이랑 요청주신 가격이 다릅니다."),
	PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "4002", "결제 정보가 없습니다."),
	PAYMENT_STATUS_NO_PEDNIND(HttpStatus.BAD_REQUEST, "4003", "결제 요청을 다시 해주세요."),

	TOSS_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "5000", "toss 결제가 실패했습니다."),
	TOSS_CANCEL_FAILED(HttpStatus.BAD_GATEWAY, "5001", "토스 결제 취소가 실패했습니다"),

    NOT_OWNER_PAYMENT(HttpStatus.BAD_REQUEST, "5020", "해당 유저의 결제가 아닙니다"),
    NOT_TOSS_PAYMENT_ORDER(HttpStatus.BAD_REQUEST, "5021", "토스 페에지에서 결제한 주문이 아닙니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "8000", "해당 주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "8001", "잘못된 주문 상태입니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "8002", "이미 취소된 주문입니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "8003", "이미 완료된 주문입니다."),
    ORDER_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "8004", "해당 주문에 대한 접근 권한이 없습니다."),
    ORDER_DELIVERY_UNAVAILABLE(HttpStatus.BAD_REQUEST, "8005", "해당 지역은 배달 불가지역입니다."),
    ORDER_CART_EMPTY(HttpStatus.BAD_REQUEST, "8006", "장바구니가 비어 있습니다."),
    ORDER_PAYMENT_MISMATCH(HttpStatus.BAD_REQUEST, "8007", "주문 결제 정보가 일치하지 않습니다."),
    ORDER_STATUS_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "8008", "현재 상태에서는 주문 상태 변경이 불가능합니다."),
    ORDER_STORE_NOT_MATCH(HttpStatus.BAD_REQUEST, "8009", "주문한 가게와 일치하지 않습니다."),
    ORDER_BELOW_MIN_PRICE(HttpStatus.BAD_REQUEST, "8010", "주문 금액이 매장의 최소 주문 금액보다 적습니다."),
    ORDER_CANCEL_TIME_EXCEEDED(HttpStatus.BAD_REQUEST, "8011", "결제 후 5분이 지나 주문을 취소할 수 없습니다."),

	MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "9000", "해당 MASTER 계정을 찾을 수 없습니다."),
	MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "9001", "해당 매니저를 찾을 수 없습니다."),
	INVALID_MANAGER_ACTION(HttpStatus.BAD_REQUEST, "9002", "유효하지 않은 매니저 상태 변경 요청입니다."),
	UNAUTHORIZED_MASTER_ONLY(HttpStatus.FORBIDDEN, "9003", "MASTER 권한이 필요한 요청입니다."),
	DUPLICATE_MANAGER_REQUEST(HttpStatus.CONFLICT, "9004", "이미 처리된 매니저 요청입니다."),
	MANAGER_STATE_CONFLICT(HttpStatus.CONFLICT, "9005", "현재 상태에서는 요청을 수행할 수 없습니다.");

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
