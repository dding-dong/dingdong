package com.sparta.dingdong.domain.menu.exception;

public class HiddenMenuAccessDeniedException extends RuntimeException {
	public HiddenMenuAccessDeniedException() {
		super("숨김 메뉴 아이템 조회 권한이 없습니다.");
	}

	public HiddenMenuAccessDeniedException(String message) {
		super(message);
	}
}
