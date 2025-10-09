package com.sparta.dingdong.domain.AI.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.domain.AI.entity.AIHistory;
import com.sparta.dingdong.domain.AI.repository.AIHistoryRepository;
import com.sparta.dingdong.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

	private final GoogleAIService googleAIService;
	private final AIHistoryRepository aiHistoryRepository;

	@Override
	public String generateDescription(String name, String ownerContent, User user) {

		String prompt = """
			당신은 전문적인 음식 카피라이터입니다.
			아래 메뉴 정보를 바탕으로 고객의 입맛을 자극하는 맛있는 설명 문구를 작성해주세요.
			
			[작성 가이드]
			- 길이는 2~3문장 정도로 짧고 명확하게
			- 재료나 특징을 자연스럽게 녹여서 설명
			- 감각적인 표현 사용 (예: 깊은 풍미, 부드러운 식감 등)
			- 너무 광고 문구처럼 과장하지 말기
			
			[메뉴 정보]
			이름: %s
			사장님 설명: %s
			
			이 메뉴의 소개 문구를 작성하세요:
			""".formatted(name, ownerContent);

		// AI 호출 로직을 GoogleAIService로 위임
		String aiContent = googleAIService.generateText(prompt);

		if (aiContent == null || aiContent.isBlank()) {
			aiContent = "AI 설명을 생성하지 못했습니다.";
		}

		log.info("aiContent: {}", aiContent);

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
