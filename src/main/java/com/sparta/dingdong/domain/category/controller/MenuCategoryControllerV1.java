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
import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;
import com.sparta.dingdong.domain.category.service.MenuCategoryItemService;
import com.sparta.dingdong.domain.category.service.MenuCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores/{storeId}/menu-categories")
@RequiredArgsConstructor
@Tag(name = "Menu Category", description = "메뉴 카테고리 관련 API")
public class MenuCategoryControllerV1 {

	private final MenuCategoryService menuCategoryService;
	private final MenuCategoryItemService menuCategoryItemService;

	/* ===================== 메뉴 카테고리 ===================== */

	@Operation(summary = "메뉴 카테고리 조회", description = "모든 사용자가 특정 가게의 메뉴 카테고리를 조회할 수 있습니다.")
	@GetMapping
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<MenuCategoryDto.Response>>> getAll(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId) {

		return ResponseEntity.ok(menuCategoryService.getByStore(storeId));
	}

	@Operation(summary = "메뉴 카테고리 생성", description = "OWNER, MANAGER, MASTER 는 메뉴 카테고리를 생성할 수 있습니다.")
	@PostMapping
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryDto.Response>> create(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Valid @RequestBody MenuCategoryDto.Request req) {

		return ResponseEntity.ok(menuCategoryService.create(storeId, req));
	}

	@Operation(summary = "메뉴 카테고리 수정", description = "OWNER, MANAGER, MASTER 는 메뉴 카테고리를 수정할 수 있습니다.")
	@PutMapping("/{menuCategoryId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryDto.Response>> update(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Valid @RequestBody MenuCategoryDto.Request req) {

		return ResponseEntity.ok(menuCategoryService.update(menuCategoryId, req));
	}

	@Operation(summary = "메뉴 카테고리 삭제", description = "OWNER, MANAGER, MASTER 는 메뉴 카테고리를 삭제할 수 있습니다.")
	@DeleteMapping("/{menuCategoryId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId) {

		return ResponseEntity.ok(menuCategoryService.delete(menuCategoryId));
	}

	@Operation(summary = "메뉴 카테고리 복구", description = "OWNER, MANAGER, MASTER 는 삭제된 메뉴 카테고리를 복구할 수 있습니다.")
	@PutMapping("/{menuCategoryId}/restore")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryDto.Response>> restore(
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId) {

		return ResponseEntity.ok(menuCategoryService.restore(menuCategoryId));
	}

	/* ===================== (메뉴)카테고리 내 메뉴 아이템 ===================== */
	// 메뉴 아이템 수정은 메뉴 패키지에서

	@Operation(summary = "(메뉴)카테고리 메뉴 조회", description = "모든 사용자가 메뉴를 조회할 수 있습니다.")
	@GetMapping("/{menuCategoryId}/items")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<MenuCategoryDto.ItemResponse>>> getItems(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId) {

		return ResponseEntity.ok(menuCategoryItemService.getItemsByCategory(menuCategoryId));
	}

	@Operation(summary = "(메뉴)카테고리에 메뉴 추가", description = "OWNER, MANAGER, MASTER 는 메뉴를 추가할 수 있습니다.")
	@PostMapping("/{menuCategoryId}/items")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryDto.ItemResponse>> addItem(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Valid @RequestBody MenuCategoryDto.ItemRequest req) {

		return ResponseEntity.ok(menuCategoryItemService.addMenuToCategory(menuCategoryId, req));
	}

	@Operation(summary = "(메뉴)카테고리 메뉴 삭제", description = "OWNER, MANAGER, MASTER 는 메뉴를 삭제할 수 있습니다.")
	@DeleteMapping("/{menuCategoryId}/items/{menuCategoryItemId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> removeItem(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Parameter(description = "메뉴아이템 UUID") @PathVariable UUID menuCategoryItemId) {

		return ResponseEntity.ok(menuCategoryItemService.removeMenuFromCategory(menuCategoryItemId));
	}
}