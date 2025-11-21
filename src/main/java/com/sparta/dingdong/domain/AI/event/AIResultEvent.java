package com.sparta.dingdong.domain.AI.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class AIResultEvent extends ApplicationEvent {
	private final UUID menuId;
	private final String aiContent;

	public AIResultEvent(Object source, UUID menuId, String aiContent) {
		super(source);
		this.menuId = menuId;
		this.aiContent = aiContent;
	}
}
