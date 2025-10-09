package com.sparta.dingdong.domain.AI.service;

import com.sparta.dingdong.domain.user.entity.User;

public interface AIService {
	String generateDescription(String name, String content, User currentUser);
}
