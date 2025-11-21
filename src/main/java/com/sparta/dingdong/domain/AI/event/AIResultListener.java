package com.sparta.dingdong.domain.AI.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AIResultListener {

	private final MenuItemRepository menuItemRepository;

	@EventListener
	@Transactional
	public void handleAIResult(AIResultEvent event) {
		menuItemRepository.findById(event.getMenuId()).ifPresent(menu -> {
			menu.setAiContent(event.getAiContent());
			menu.setIsAiUsed(true);
			// 필요시 다른 후속 처리 가능
		});
	}
}
