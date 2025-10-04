package com.sparta.dingdong.domain.user.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.user.dto.response.ManagerResponseDto;
import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerService {

	private final ManagerRepository managerRepository;

	/** 승인 대기중 매니저 목록 조회 */
	@Transactional(readOnly = true)
	public List<ManagerResponseDto> getPendingManagers() {
		return managerRepository.findByStatus(ManagerStatus.PENDING)
			.stream()
			.map(ManagerResponseDto::new)
			.collect(Collectors.toList());
	}

	/** 매니저 승인 (PENDING → APPROVED) */
	@Transactional
	public void approveManager(UUID userId) {
		Manager manager = getManagerByUserId(userId);
		if (manager.getManagerStatus() != ManagerStatus.PENDING) {
			throw new IllegalStateException("대기 상태의 매니저만 승인할 수 있습니다.");
		}
		manager.setManagerStatus(ManagerStatus.APPROVED);
	}

	/** 매니저 활성화 (APPROVED → ACTIVE) */
	@Transactional
	public void activateManager(UUID userId) {
		Manager manager = getManagerByUserId(userId);
		if (manager.getManagerStatus() != ManagerStatus.APPROVED) {
			throw new IllegalStateException("승인된 매니저만 활성화할 수 있습니다.");
		}
		manager.setManagerStatus(ManagerStatus.ACTIVE);
	}

	/** 매니저 정지 (ACTIVE → SUSPENDED) */
	@Transactional
	public void suspendManager(UUID userId) {
		Manager manager = getManagerByUserId(userId);
		if (manager.getManagerStatus() != ManagerStatus.ACTIVE) {
			throw new IllegalStateException("활성화된 매니저만 정지할 수 있습니다.");
		}
		manager.setManagerStatus(ManagerStatus.SUSPENDED);
	}

	/** 매니저 거절 (PENDING → DELETED) */
	@Transactional
	public void rejectManager(UUID userId) {
		Manager manager = getManagerByUserId(userId);
		if (manager.getManagerStatus() != ManagerStatus.PENDING) {
			throw new IllegalStateException("대기중 매니저만 거절할 수 있습니다.");
		}
		manager.setManagerStatus(ManagerStatus.DELETED);
	}

	/** 매니저 삭제 */
	@Transactional
	public void deleteManager(UUID userId) {
		Manager manager = getManagerByUserId(userId);
		managerRepository.delete(manager);
	}

	private Manager getManagerByUserId(UUID userId) {
		return managerRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 매니저를 찾을 수 없습니다."));
	}
}
