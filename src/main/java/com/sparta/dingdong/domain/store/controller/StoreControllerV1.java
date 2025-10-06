package com.sparta.dingdong.domain.store.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.store.dto.StoreDto;
import com.sparta.dingdong.domain.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 관련 API")
public class StoreControllerV1 {

	private final StoreService storeService;

	/* ==================== PUBLIC 기능 ==================== */

	@Operation(summary = "가게 전체 목록 조회 (삭제되지 않은 가게)", description = "고객[비회원/OWNER/CUSTOMER]은 소프트 딜리트되지 않은 가게만 조회합니다.")
	@GetMapping("/list")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<StoreDto.Response>>> getActiveStores() {
		return ResponseEntity.ok(storeService.getActiveStores());
	}

	@Operation(summary = "카테고리별 가게 목록 조회 (삭제되지 않은 가게)", description = "고객[비회원/OWNER/CUSTOMER]은 소프트 딜리트되지 않은 가게만 조회합니다.")
	@GetMapping("/{storeCategoryId}/list")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<StoreDto.Response>>> getActiveStoresByCategory(
		@PathVariable UUID storeCategoryId) {
		return ResponseEntity.ok(storeService.getActiveStoresByCategory(storeCategoryId));
	}

	/* ==================== OWNER 기능 ==================== */

	@Operation(summary = "가게 등록", description = "OWNER는 새로운 가게를 등록할 수 있습니다.")
	@PostMapping
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> create(
		@Valid @RequestBody StoreDto.Request req) {
		return ResponseEntity.ok(storeService.create(req));
	}

	@Operation(summary = "내 가게 전체 조회", description = "OWNER는 본인이 등록한 모든 가게를 조회합니다.")
	@GetMapping("/my")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<List<StoreDto.Response>>> getMyStores() {
		return ResponseEntity.ok(storeService.getMyStores());
	}

	@Operation(summary = "내 가게 상세 조회", description = "OWNER는 본인의 가게 상세를 조회합니다.")
	@GetMapping("/my/{storeId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> getMyStore(@PathVariable UUID storeId) {
		return ResponseEntity.ok(storeService.getMyStore(storeId));
	}

	@Operation(summary = "가게 수정", description = "OWNER는 본인의 가게 정보를 수정할 수 있습니다.")
	@PutMapping("/my/{storeId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> update(
		@PathVariable UUID storeId,
		@Valid @RequestBody StoreDto.Request req) {
		return ResponseEntity.ok(storeService.update(storeId, req));
	}

	@Operation(summary = "가게 영업 상태 수정", description = "OWNER는 본인의 가게 영업 상태를 PREPARING/OPEN/CLOSED로 수정할 수 있습니다.")
	@PatchMapping("/my/{storeId}/status")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> updateStatus(
		@PathVariable UUID storeId,
		@RequestBody StoreDto.UpdateStatusRequest req) {
		return ResponseEntity.ok(storeService.updateStatus(storeId, req));
	}

	// TODO : OWNER는 가게 삭제 요청을 할 수 있도록 변경 --> 문의가 없는데..

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Operation(summary = "가게 전체 목록 조회", description = "MANAGER/MASTER는 모든 상태의 가게를 조회할 수 있습니다.")
	@GetMapping("/admin/list")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<List<StoreDto.Response>>> getAll() {
		return ResponseEntity.ok(storeService.getAll());
	}

	@Operation(summary = "카테고리별 전체 가게 조회", description = "MANAGER/MASTER는 모든 상태의 가게를 조회할 수 있습니다.")
	@GetMapping("/admin/{storeCategoryId}/list")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<List<StoreDto.Response>>> getAllByCategory(
		@PathVariable UUID storeCategoryId) {
		return ResponseEntity.ok(storeService.getAllByCategory(storeCategoryId));
	}

	@Operation(summary = "가게 상세 조회", description = "MANAGER는 특정 가게의 상세를 조회할 수 있습니다.")
	@GetMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> getById(@PathVariable UUID storeId) {
		return ResponseEntity.ok(storeService.getById(storeId));
	}

	@Operation(summary = "가게 상세 수정", description = "MANAGER/MASTER는 특정 가게의 상세 정보를 수정할 수 있습니다.")
	@PutMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> manageUpdate(
		@PathVariable UUID storeId,
		@Valid @RequestBody StoreDto.Request req) {
		return ResponseEntity.ok(storeService.manageUpdate(storeId, req));
	}

	@Operation(summary = "가게 영업 상태 강제 변경", description = "MANAGER/MASTER는 가게 영업 상태를 강제로 변경할 수 있습니다.")
	@PatchMapping("/admin/{storeId}/force-status")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> forceUpdateStatus(
		@PathVariable UUID storeId,
		@RequestBody StoreDto.UpdateStatusRequest req) {
		return ResponseEntity.ok(storeService.forceUpdateStatus(storeId, req));
	}

	@Operation(summary = "가게 삭제", description = "MANAGER/MASTER는 특정 가게를 삭제할 수 있습니다.")
	@DeleteMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(@PathVariable UUID storeId) {
		return ResponseEntity.ok(storeService.delete(storeId));
	}

	@Operation(summary = "가게 복구", description = "MANAGER/MASTER는 삭제된 특정 가게를 복구할 수 있습니다.")
	@PutMapping("/admin/{storeId}/restore")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreDto.Response>> restore(@PathVariable UUID storeId) {
		return ResponseEntity.ok(storeService.restore(storeId));
	}
}