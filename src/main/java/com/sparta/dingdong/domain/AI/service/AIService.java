package com.sparta.dingdong.domain.AI.service;

import java.util.UUID;

import com.sparta.dingdong.domain.user.entity.User;

import reactor.core.publisher.Mono;

public interface AIService {
	Mono<String> generateDescription(String name, String content, User currentUser, UUID menuId);
}
