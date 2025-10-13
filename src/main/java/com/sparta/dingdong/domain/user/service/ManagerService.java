package com.sparta.dingdong.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.user.dto.response.ManagerResponseDto;
import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerService {

	private final ManagerRepository managerRepository;

	@Transactional(readOnly = true)
	public List<ManagerResponseDto> getAllManagers() {
		return managerRepository.findAll().stream()
			.map(ManagerResponseDto::new)
			.collect(Collectors.toList());
	}

	@Transactional
	public void updateManagerStatus(Long managerId, String action, Long masterId) {
		Manager manager = managerRepository.findById(managerId)
			.orElseThrow(() -> new IllegalArgumentException("해당 매니저를 찾을 수 없습니다."));

		switch (action.toLowerCase()) {
			case "approve" -> manager.approve();
			case "activate" -> manager.activate();
			case "suspend" -> manager.suspend();
			case "reject" -> manager.reject();
			case "delete" -> manager.softDeleteByMaster(masterId);
			default -> throw new IllegalArgumentException("유효하지 않은 요청입니다: " + action);
		}
	}

}
