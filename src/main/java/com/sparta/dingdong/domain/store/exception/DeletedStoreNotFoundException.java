package com.sparta.dingdong.domain.store.exception;

public class DeletedStoreNotFoundException extends RuntimeException {
	public DeletedStoreNotFoundException() {
		super("삭제된 가게를 찾을 수 없습니다.");
	}

	public DeletedStoreNotFoundException(String message) {
		super(message);
	}
}
