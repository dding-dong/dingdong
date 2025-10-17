package com.sparta.dingdong.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.ManagerStatusRequestDto;
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
	public ResponseEntity<BaseResponseDto<List<ManagerResponseDto>>> getAllManager(
		@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.ok(BaseResponseDto.success("매니저 조회 성공", managerService.getAllManagers()));
	}

	@PreAuthorize("hasRole('MASTER')")
	@PatchMapping("/status/{managerId}")
	public ResponseEntity<BaseResponseDto<Void>> updateManagerStatus(
		@AuthenticationPrincipal UserAuth userAuth,
		@RequestBody ManagerStatusRequestDto request,
		@PathVariable Long managerId
	) {
		managerService.updateManagerStatus(userAuth, request, managerId);
		return ResponseEntity.ok(BaseResponseDto.success("매니저 상태가 성공적으로 변경되었습니다."));
	}

}
