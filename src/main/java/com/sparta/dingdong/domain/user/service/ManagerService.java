package com.sparta.dingdong.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.exception.CommonErrorCode;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.ManagerStatusRequest;
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
	public void updateManagerStatus(UserAuth userAuth, ManagerStatusRequest request, Long managerId) {
		Manager manager = managerRepository.findById(managerId)
			.orElseThrow(() -> new IllegalArgumentException(
				CommonErrorCode.MANAGER_NOT_FOUND.getMessage()
			));

		manager.updateStatus(request.getStatus());
	}

}
