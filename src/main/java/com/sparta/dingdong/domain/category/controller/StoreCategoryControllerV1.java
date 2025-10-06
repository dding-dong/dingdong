package com.sparta.dingdong.domain.category.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.category.dto.StoreCategoryDto;
import com.sparta.dingdong.domain.category.service.StoreCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
	public ResponseEntity<BaseResponseDto<List<StoreCategoryDto.Response>>> getAll() {
		return ResponseEntity.ok(storeCategoryService.getAll());
	}

	@Operation(summary = "가게 카테고리 조회", description = "UUID로 가게 카테고리를 조회합니다.")
	@GetMapping("/{storeCategoryId}")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<StoreCategoryDto.Response>> getById(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId) {
		return ResponseEntity.ok(storeCategoryService.getById(storeCategoryId));
	}

	@Operation(
		summary = "가게 카테고리 생성",
		description = "관리자가 가게 카테고리를 생성합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "가게 카테고리 생성 요청",
			required = true,
			content = @Content(
				schema = @Schema(implementation = StoreCategoryDto.Request.class),
				examples = {
					@ExampleObject(
						name = "요청 예시",
						value = "{ \"name\": \"한식\", \"description\": \"한식 전문점\" }"
					)
				}
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "생성 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = StoreCategoryDto.Response.class),
					examples = @ExampleObject(
						name = "응답 예시",
						value = "{ \"status\": \"SUCCESS\", \"code\": \"200\", \"message\": \"카테고리 생성 성공\", \"data\": { \"id\": \"abb3b4cf-c378-4c55-b7da-123456789012\", \"name\": \"한식\", \"description\": \"한식 전문점\" } }"
					)
				)
			),
			@ApiResponse(responseCode = "400", description = "잘못된 요청"),
			@ApiResponse(responseCode = "401", description = "권한 없음")
		}
	)
	@PostMapping
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryDto.Response>> create(
		@Valid @RequestBody StoreCategoryDto.Request req) {
		return ResponseEntity.ok(storeCategoryService.create(req));
	}

	@Operation(summary = "가게 카테고리 수정", description = "관리자는 가게 카테고리를 수정합니다.")
	@PutMapping("/{storeCategoryId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryDto.Response>> update(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId,
		@Valid @RequestBody StoreCategoryDto.Request req) {
		return ResponseEntity.ok(storeCategoryService.update(storeCategoryId, req));
	}

	@Operation(summary = "가게 카테고리 삭제", description = "관리자는 가게 카테고리를 소프트 삭제합니다.")
	@DeleteMapping("/{storeCategoryId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId) {
		return ResponseEntity.ok(storeCategoryService.delete(storeCategoryId));
	}

	@Operation(summary = "가게 카테고리 복구", description = "관리자가 소프트 삭제된 가게 카테고리를 복구합니다.")
	@PostMapping("/{storeCategoryId}/restore")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<StoreCategoryDto.Response>> restore(
		@Parameter(description = "가게 카테고리 UUID") @PathVariable UUID storeCategoryId) {
		return ResponseEntity.ok(storeCategoryService.restore(storeCategoryId));
	}
}