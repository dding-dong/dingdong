package com.sparta.dingdong.domain.menu.exception;

public class DeletedStoreMenuAccessException extends RuntimeException {
	public DeletedStoreMenuAccessException() {
		super("삭제된 가게의 메뉴 아이템은 조회할 수 없습니다.");
	}

	public DeletedStoreMenuAccessException(String message) {
		super(message);
	}
}
