package com.sparta.dingdong.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.user.dto.response.ManagerResponseDto;
import com.sparta.dingdong.domain.user.service.ManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/master/managers")
@RequiredArgsConstructor
public class ManagerController {

	private final ManagerService managerService;

	/** 승인 대기중 목록 조회 */
	@GetMapping("/pending")
	public ResponseEntity<BaseResponseDto<List<ManagerResponseDto>>> getPendingManagers() {
		List<ManagerResponseDto> pendingManagers = managerService.getPendingManagers();
		return ResponseEntity.ok(
			BaseResponseDto.success("승인 대기중 매니저 목록 조회 성공", pendingManagers)
		);
	}

	/** 매니저 승인 */
	@PostMapping("/{userId}/approve")
	public ResponseEntity<String> approveManager(@PathVariable Long userId) {
		managerService.approveManager(userId);
		return ResponseEntity.ok("매니저 승인 완료");
	}

	/** 매니저 활성화 */
	@PostMapping("/{userId}/activate")
	public ResponseEntity<String> activateManager(@PathVariable Long userId) {
		managerService.activateManager(userId);
		return ResponseEntity.ok("매니저 활성화 완료");
	}

	/** 매니저 정지 */
	@PostMapping("/{userId}/suspend")
	public ResponseEntity<String> suspendManager(@PathVariable Long userId) {
		managerService.suspendManager(userId);
		return ResponseEntity.ok("매니저 정지 완료");
	}

	/** 매니저 거절 */
	@PostMapping("/{userId}/reject")
	public ResponseEntity<String> rejectManager(@PathVariable Long userId) {
		managerService.rejectManager(userId);
		return ResponseEntity.ok("매니저 승인 거절");
	}

	/** 매니저 삭제 */
	@DeleteMapping("/{userId}/delete")
	public ResponseEntity<String> deleteManager(@PathVariable Long userId) {
		managerService.deleteManager(userId);
		return ResponseEntity.ok("매니저 삭제 완료");
	}
}
