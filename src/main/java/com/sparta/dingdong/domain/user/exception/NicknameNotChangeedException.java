package com.sparta.dingdong.domain.user.exception;

public class NicknameNotChangeedException extends RuntimeException {

	public NicknameNotChangeedException() {
		super("현재 계정의 닉네임과 같습니다.");
	}

	public NicknameNotChangeedException(String message) {
		super(message);
	}
}
