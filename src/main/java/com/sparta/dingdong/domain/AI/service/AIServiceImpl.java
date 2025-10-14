package com.sparta.dingdong.domain.AI.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.domain.AI.entity.AIHistory;
import com.sparta.dingdong.domain.AI.prompt.PromptBuilder;
import com.sparta.dingdong.domain.AI.repository.AIHistoryRepository;
import com.sparta.dingdong.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

	private final PromptBuilder promptBuilder;
	private final GoogleAIService googleAIService;
	private final AIHistoryRepository aiHistoryRepository;

	@Override
	public String generateDescription(String name, String ownerContent, User user) {

		// 프롬프트 생성
		String prompt = promptBuilder.buildMenuDescriptionPrompt(name, ownerContent);

		// AI 호출 로직을 GoogleAIService로 위임
		String aiContent = googleAIService.generateText(prompt);

		if (aiContent == null || aiContent.isBlank()) {
			aiContent = "AI 설명을 생성하지 못했습니다.";
		}

		// AI 히스토리 저장
		AIHistory history = AIHistory.builder()
			.id(UUID.randomUUID())
			.question(prompt)
			.answer(aiContent)
			.owner(user)
			.build();
		aiHistoryRepository.save(history);
		
		return aiContent;
	}
}
