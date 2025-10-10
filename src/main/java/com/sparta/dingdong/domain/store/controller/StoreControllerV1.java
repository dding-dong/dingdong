package com.sparta.dingdong.domain.store.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.dto.PageResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;
import com.sparta.dingdong.domain.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 API")
public class StoreControllerV1 {

	private final StoreService storeService;

	/* ==================== PUBLIC 기능 ==================== */

	@Operation(summary = "가게 전체 목록 조회 (삭제되지 않은 가게)", description = "모든 사용자는 소프트 삭제되지 않은 가게만 조회합니다.")
	@GetMapping("/list")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<PageResponseDto<StoreResponseDto>>> getActiveStores(
		@Parameter(description = "검색 키워드 (가게명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable,
		@AuthenticationPrincipal UserAuth user
	) {
		Page<StoreResponseDto> list = storeService.getActiveStores(keyword, pageable, user);
		PageResponseDto<StoreResponseDto> result = PageResponseDto.<StoreResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("활성화된 가게 목록 조회 성공", result));
	}

	@Operation(summary = "가게 카테고리별 가게 목록 조회 (삭제되지 않은 가게)", description = "모든 사용자는 소프트 삭제되지 않은 가게만 조회합니다.")
	@GetMapping("/{storeCategoryId}/list")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<PageResponseDto<StoreResponseDto>>> getActiveStoresByCategory(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId,
		@Parameter(description = "검색 키워드 (가게명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable,
		@AuthenticationPrincipal UserAuth user) {
		Page<StoreResponseDto> list = storeService.getActiveStoresByCategory(storeCategoryId, keyword, pageable,
			user);
		PageResponseDto<StoreResponseDto> result = PageResponseDto.<StoreResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("활성화된 카테고리 가게 목록 조회 성공", result));
	}

	/* ==================== OWNER 기능 ==================== */

	@Operation(summary = "가게 등록", description = "사장님은 새로운 가게를 등록할 수 있습니다.")
	@PostMapping
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> create(
		@Valid @RequestBody StoreRequestDto req, @AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.create(req, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 생성 성공", result));
	}

	@Operation(summary = "내 가게 전체 조회", description = "사장님은 본인이 등록한 모든 가게를 조회합니다.")
	@GetMapping("/my")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<PageResponseDto<StoreResponseDto>>> getMyStores(
		@Parameter(description = "검색 키워드 (가게명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable,
		@AuthenticationPrincipal UserAuth user) {
		Page<StoreResponseDto> list = storeService.getMyStores(keyword, pageable, user);
		PageResponseDto<StoreResponseDto> result = PageResponseDto.<StoreResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("내 가게 조회 성공", result));
	}

	@Operation(summary = "내 가게 상세 조회", description = "사장님은 본인의 가게 상세를 조회합니다.")
	@GetMapping("/my/{storeId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> getMyStore(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.getMyStore(storeId, user);
		return ResponseEntity.ok(BaseResponseDto.success("내 가게 조회 성공", result));
	}

	@Operation(summary = "가게 수정", description = "사장님은 본인의 가게 정보를 수정할 수 있습니다.")
	@PutMapping("/my/{storeId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> update(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Valid @RequestBody StoreRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.update(storeId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 정보 수정 성공", result));
	}

	@Operation(summary = "가게 영업 상태 수정", description = "사장님은 본인의 가게 영업 상태를 PREPARING/OPEN/CLOSED로 수정할 수 있습니다.")
	@PatchMapping("/my/{storeId}/status")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> updateStatus(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@RequestBody StoreUpdateStatusRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.updateStatus(storeId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 상태 업데이트 성공", result));
	}

	/* ==================== MANAGER/MASTER 기능 ==================== */

	@Operation(summary = "가게 전체 목록 조회", description = "관리자는 모든 상태의 가게를 조회할 수 있습니다.")
	@GetMapping("/admin/list")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<PageResponseDto<StoreResponseDto>>> getAll(
		@Parameter(description = "검색 키워드 (가게명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable,
		@AuthenticationPrincipal UserAuth user) {
		Page<StoreResponseDto> list = storeService.getAll(keyword, pageable, user);
		PageResponseDto<StoreResponseDto> result = PageResponseDto.<StoreResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("전체 가게 조회 성공", result));
	}

	@Operation(summary = "가게 카테고리별 전체 가게 조회", description = "관리자는 모든 상태의 가게를 조회할 수 있습니다.")
	@GetMapping("/admin/{storeCategoryId}/list")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<PageResponseDto<StoreResponseDto>>> getAllByCategory(
		@Parameter(description = "가게 키테고리 UUID") @PathVariable UUID storeCategoryId,
		@Parameter(description = "검색 키워드 (가게명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable,
		@AuthenticationPrincipal UserAuth user) {
		Page<StoreResponseDto> list = storeService.getAllByCategory(storeCategoryId, keyword, pageable, user);
		PageResponseDto<StoreResponseDto> result = PageResponseDto.<StoreResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("카테고리별 가게 조회 성공", result));
	}

	@Operation(summary = "가게 상세 조회", description = "관리자는 특정 가게의 상세를 조회할 수 있습니다.")
	@GetMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> getById(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.getById(storeId, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 조회 성공", result));
	}

	@Operation(summary = "가게 상세 수정", description = "관리자는 특정 가게의 상세 정보를 수정할 수 있습니다.")
	@PutMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> manageUpdate(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Valid @RequestBody StoreRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.manageUpdate(storeId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 정보 관리자 수정 성공", result));
	}

	@Operation(summary = "가게 영업 상태 강제 변경", description = "관리자는 가게 영업 상태를 강제로 변경할 수 있습니다.")
	@PatchMapping("/admin/{storeId}/force-status")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> forceUpdateStatus(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@RequestBody StoreUpdateStatusRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.forceUpdateStatus(storeId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 강제 상태 변경 성공", result));
	}

	@Operation(summary = "가게 삭제", description = "관리자는 가게를 삭제할 수 있습니다.")
	@DeleteMapping("/admin/{storeId}")
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@AuthenticationPrincipal UserAuth user) {
		storeService.delete(storeId, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 삭제 성공"));
	}

	@Operation(summary = "가게 복구", description = "관리자는 삭제된 가게를 복구할 수 있습니다.")
	@PutMapping("/admin/{storeId}/restore")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreResponseDto>> restore(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@AuthenticationPrincipal UserAuth user) {
		StoreResponseDto result = storeService.restore(storeId, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 복구 성공", result));
	}
}
