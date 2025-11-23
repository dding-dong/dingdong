package com.sparta.dingdong.domain.AI.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.sparta.dingdong.domain.user.entity.User;

public interface AIService {

	/**
	 * 비동기로 AI 메뉴 설명을 생성하고, 완료 시 이벤트를 발행
	 *
	 * @param name 메뉴 이름
	 * @param content 메뉴 내용
	 * @param currentUser 요청한 사용자
	 * @param menuId 생성된 메뉴 엔티티 ID
	 * @return AI 생성 결과 문자열을 담은 CompletableFuture
	 */
	CompletableFuture<String> generateDescription(String name, String content, User currentUser, UUID menuId);
}
