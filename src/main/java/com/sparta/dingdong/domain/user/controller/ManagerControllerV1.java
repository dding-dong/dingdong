package com.sparta.dingdong.domain.user.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.domain.user.dto.request.ManagerStatusRequest;
import com.sparta.dingdong.domain.user.dto.response.ManagerResponseDto;
import com.sparta.dingdong.domain.user.service.ManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/managers")
@RequiredArgsConstructor
public class ManagerControllerV1 {

	private final ManagerService managerService;

	@PreAuthorize("hasRole('MASTER')")
	@GetMapping
	public List<ManagerResponseDto> getManagers(
		@AuthenticationPrincipal(expression = "userRole.value") String role
	) {
		if (!role.equals("MASTER")) {
			throw new SecurityException("MASTER 권한이 필요합니다.");
		}
		return managerService.getAllManagers();
	}

	@PreAuthorize("hasRole('MASTER')")
	@PatchMapping
	public String updateManagerStatus(
		@AuthenticationPrincipal(expression = "id") Long masterId,
		@RequestBody ManagerStatusRequest request
	) {
		managerService.updateManagerStatus(request.getManagerId(), request.getAction(), masterId);
		return "매니저 상태가 성공적으로 변경되었습니다.";
	}

}
