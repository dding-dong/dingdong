package com.sparta.dingdong.domain.menu.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.menu.dto.MenuItemDto;
import com.sparta.dingdong.domain.menu.service.MenuItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Item", description = "메뉴 관련 API")
public class MenuItemControllerV1 {

	private final MenuItemService menuItemService;

	@Operation(summary = "가게 메뉴 전체 조회", description = "모든 사용자는 메뉴를 조회할 수 있습니다. (사장님은 숨김 메뉴 포함 조회 가능)")
	@GetMapping
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<List<MenuItemDto.Response>>> getAll(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "숨김 포함 여부") @RequestParam(defaultValue = "false") boolean includeHidden
	) {
		return ResponseEntity.ok(menuItemService.getAllByStore(storeId, includeHidden));
	}

	@Operation(summary = "메뉴 등록", description = "OWNER와 MANAGER/MASTER는 메뉴를 등록할 수 있습니다. (AI 설명 생성 옵션 가능)")
	@PostMapping
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuItemDto.Response>> create(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Valid @RequestBody MenuItemDto.Request req
	) {
		return ResponseEntity.ok(menuItemService.create(storeId, req));
	}

	@Operation(summary = "메뉴 상세 조회", description = "OWNER와 MANAGER/MASTER는 가게의 특정 메뉴 상세정보를 조회할 수 있습니다.")
	@GetMapping("/{menuItemId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuItemDto.Response>> getDetail(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 UUID") @PathVariable UUID menuItemId
	) {
		return ResponseEntity.ok(menuItemService.getById(menuItemId));
	}

	@Operation(summary = "메뉴 수정", description = "OWNER와 MANAGER/MASTER는 메뉴 정보를 수정할 수 있습니다. (숨김, 품절, 내용 변경 가능)")
	@PutMapping("/{menuItemId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuItemDto.Response>> update(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 UUID") @PathVariable UUID menuItemId,
		@Valid @RequestBody MenuItemDto.Request req
	) {
		return ResponseEntity.ok(menuItemService.update(menuItemId, req));
	}

	@Operation(summary = "메뉴 삭제", description = "OWNER와 MANAGER/MASTER는 메뉴를 삭제할 수 있습니다.")
	@DeleteMapping("/{menuItemId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 UUID") @PathVariable UUID menuItemId
	) {
		return ResponseEntity.ok(menuItemService.delete(menuItemId));
	}
}