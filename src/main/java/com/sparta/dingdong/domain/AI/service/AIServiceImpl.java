package com.sparta.dingdong.domain.AI.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AIServiceImpl implements AIService {
	@Override
	public String generateDescription(String name, String content) {
		// OpenAI API 호출
		return "";
	}
}
