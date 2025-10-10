package com.sparta.dingdong.domain.user.exception;

public class NoUpdateTargetException extends RuntimeException {

	public NoUpdateTargetException() {
		super("닉네임이나 새 비밀번호 중 하나는 반드시 입력되어야 합니다.");
	}

	public NoUpdateTargetException(String message) {
		super(message);
	}
}
