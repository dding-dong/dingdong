package com.sparta.dingdong.domain.category.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.category.dto.request.StoreCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.StoreCategoryResponseDto;
import com.sparta.dingdong.domain.category.service.StoreCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/store-categories")
@RequiredArgsConstructor
@Tag(name = "Store Category", description = "가게 카테고리 관련 API")
public class StoreCategoryControllerV1 {

	private final StoreCategoryService storeCategoryService;

	@Operation(summary = "가게 카테고리 목록 조회", description = "가게 카테고리를 조회합니다.")
	@GetMapping
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<StoreCategoryResponseDto>>> getAll() {
		List<StoreCategoryResponseDto> result = storeCategoryService.getAll();
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 목록 조회 성공", result));
	}

	@Operation(summary = "가게 카테고리 조회", description = "UUID로 가게 카테고리를 조회합니다.")
	@GetMapping("/{storeCategoryId}")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<StoreCategoryResponseDto>> getById(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId) {
		StoreCategoryResponseDto result = storeCategoryService.getById(storeCategoryId);
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 조회 성공", result));
	}

	@Operation(summary = "가게 카테고리 생성", description = "관리자가 가게 카테고리를 생성합니다.")
	@PostMapping
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryResponseDto>> create(
		@Valid @RequestBody StoreCategoryRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreCategoryResponseDto result = storeCategoryService.create(req);
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 생성 성공", result));
	}

	@Operation(summary = "가게 카테고리 수정", description = "관리자가 가게 카테고리를 수정합니다.")
	@PutMapping("/{storeCategoryId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryResponseDto>> update(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId,
		@Valid @RequestBody StoreCategoryRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		StoreCategoryResponseDto result = storeCategoryService.update(storeCategoryId, req);
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 수정 성공", result));
	}

	@Operation(summary = "가게 카테고리 삭제", description = "관리자가 가게 카테고리를 소프트 삭제합니다.")
	@DeleteMapping("/{storeCategoryId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId,
		@AuthenticationPrincipal UserAuth user) {
		storeCategoryService.delete(storeCategoryId, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 삭제 성공"));
	}

	@Operation(summary = "가게 카테고리 복구", description = "관리자가 소프트 삭제된 가게 카테고리를 복구합니다.")
	@PostMapping("/{storeCategoryId}/restore")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryResponseDto>> restore(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId,
		@AuthenticationPrincipal UserAuth user) {
		StoreCategoryResponseDto result = storeCategoryService.restore(storeCategoryId, user);
		return ResponseEntity.ok(BaseResponseDto.success("가게 카테고리 복구 성공", result));
	}
}
