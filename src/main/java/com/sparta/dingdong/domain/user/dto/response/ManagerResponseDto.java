package com.sparta.dingdong.domain.user.dto.response;

import java.time.LocalDateTime;

import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;

import lombok.Getter;

@Getter
public class ManagerResponseDto {

	private Long id;
	private ManagerStatus status;
	private LocalDateTime createdAt;

	public ManagerResponseDto(Manager entity) {
		this.id = entity.getId();
		this.status = entity.getManagerStatus();
		this.createdAt = entity.getCreatedAt();
	}
}

