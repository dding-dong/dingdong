package com.sparta.dingdong.domain.user.dto.response;

import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;

import lombok.Getter;

@Getter
public class ManagerResponseDto {

	private Long id;
	private ManagerStatus status;

	public ManagerResponseDto(Manager entity) {
		this.id = entity.getId();
		this.status = entity.getManagerStatus();
	}
}

