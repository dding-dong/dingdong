package com.sparta.dingdong.domain.AI.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sparta.dingdong.domain.AI.entity.AIHistory;
import com.sparta.dingdong.domain.AI.event.AIResultEvent;
import com.sparta.dingdong.domain.AI.prompt.PromptBuilder;
import com.sparta.dingdong.domain.AI.repository.AIHistoryRepository;
import com.sparta.dingdong.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

	private final PromptBuilder promptBuilder;
	private final GoogleAIService googleAIService;
	private final AIHistoryRepository aiHistoryRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Async
	@Override
	public Mono<String> generateDescription(String name, String ownerContent, User user, UUID menuId) {
		String prompt = promptBuilder.buildMenuDescriptionPrompt(name, ownerContent);

		return googleAIService.generateText(prompt)
			.defaultIfEmpty("AI 설명을 생성하지 못했습니다.")
			.doOnNext(aiContent -> {
				// JPA 저장 (동기)
				AIHistory history = AIHistory.builder()
					.id(UUID.randomUUID())
					.question(prompt)
					.answer(aiContent)
					.owner(user)
					.build();
				aiHistoryRepository.save(history);

				// 이벤트 발행
				eventPublisher.publishEvent(new AIResultEvent(this, menuId, aiContent));
			})
			.onErrorReturn("AI 설명을 생성하지 못했습니다.");
	}
}
