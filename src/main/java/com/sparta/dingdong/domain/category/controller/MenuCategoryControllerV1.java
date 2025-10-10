package com.sparta.dingdong.domain.category.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.dto.PageResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.common.util.PageableUtils;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryItemRequestDto;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryItemResponseDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryResponseDto;
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
@Tag(name = "Menu Category", description = "메뉴 카테고리 API")
public class MenuCategoryControllerV1 {

	private final MenuCategoryService menuCategoryService;
	private final MenuCategoryItemService menuCategoryItemService;

	/* ===================== 메뉴 카테고리 ===================== */

	@Operation(summary = "메뉴 카테고리 조회", description = "가게의 메뉴 카테고리를 조회합니다.")
	@GetMapping
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<PageResponseDto<MenuCategoryResponseDto>>> getAll(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "검색 키워드 (메뉴 카테고리명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable) {
		Pageable fixedPageable = PageableUtils.fixedPageable(pageable, "createdAt");
		Page<MenuCategoryResponseDto> list = menuCategoryService.getByStore(storeId, keyword, pageable);
		PageResponseDto<MenuCategoryResponseDto> result = PageResponseDto.<MenuCategoryResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("메뉴 카테고리 목록 조회 성공", result));
	}

	@Operation(summary = "메뉴 카테고리 생성", description = "사장님과 관리자는 메뉴 카테고리를 생성할 수 있습니다.")
	@PostMapping
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryResponseDto>> create(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Valid @RequestBody MenuCategoryRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		MenuCategoryResponseDto result = menuCategoryService.create(storeId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("메뉴 카테고리 생성 성공", result));
	}

	@Operation(summary = "메뉴 카테고리 수정", description = "사장님과 관리자는 메뉴 카테고리를 수정할 수 있습니다.")
	@PutMapping("/{menuCategoryId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryResponseDto>> update(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Valid @RequestBody MenuCategoryRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		MenuCategoryResponseDto result = menuCategoryService.update(menuCategoryId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("메뉴 카테고리 수정 성공", result));
	}

	@Operation(summary = "메뉴 카테고리 삭제", description = "사장님과 관리자는 메뉴 카테고리를 삭제할 수 있습니다.")
	@DeleteMapping("/{menuCategoryId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> delete(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@AuthenticationPrincipal UserAuth user) {
		menuCategoryService.delete(menuCategoryId, user);
		return ResponseEntity.ok(BaseResponseDto.success("메뉴 카테고리 삭제 성공"));
	}

	@Operation(summary = "메뉴 카테고리 복구", description = "사장님과 관리자는 삭제된 메뉴 카테고리를 복구할 수 있습니다.")
	@PutMapping("/{menuCategoryId}/restore")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryResponseDto>> restore(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@AuthenticationPrincipal UserAuth user) {
		MenuCategoryResponseDto result = menuCategoryService.restore(menuCategoryId, user);
		return ResponseEntity.ok(BaseResponseDto.success("메뉴 카테고리 복구 성공", result));
	}

	/* ===================== 메뉴 카테고리 내 메뉴 아이템 ===================== */

	@Operation(summary = "카테고리별 메뉴 조회", description = "모든 사용자가 메뉴를 조회할 수 있습니다.")
	@GetMapping("/{menuCategoryId}/items")
	@PreAuthorize("true")
	public ResponseEntity<BaseResponseDto<PageResponseDto<MenuCategoryItemResponseDto>>> getItems(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Parameter(description = "검색 키워드 (메뉴명)") @RequestParam(required = false) String keyword,
		@ParameterObject Pageable pageable) {
		Pageable fixedPageable = PageableUtils.fixedPageable(pageable, "createdAt");
		Page<MenuCategoryItemResponseDto> list = menuCategoryItemService.getItemsByCategory(menuCategoryId, keyword,
			pageable);
		PageResponseDto<MenuCategoryItemResponseDto> result = PageResponseDto.<MenuCategoryItemResponseDto>builder()
			.content(list.getContent())
			.totalElements(list.getTotalElements())
			.totalPages(list.getTotalPages())
			.pageNumber(list.getNumber())
			.pageSize(list.getSize())
			.first(list.isFirst())
			.last(list.isLast())
			.build();
		return ResponseEntity.ok(BaseResponseDto.success("카테고리별 메뉴 조회 성공", result));
	}

	@Operation(summary = "카테고리에 메뉴 추가", description = "사장님과 관리자는 메뉴를 추가할 수 있습니다.")
	@PostMapping("/{menuCategoryId}/items")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<MenuCategoryItemResponseDto>> addItem(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Valid @RequestBody MenuCategoryItemRequestDto req,
		@AuthenticationPrincipal UserAuth user) {
		MenuCategoryItemResponseDto result = menuCategoryItemService.addMenuToCategory(menuCategoryId, req, user);
		return ResponseEntity.ok(BaseResponseDto.success("카테고리에 메뉴 추가 성공", result));
	}

	@Operation(summary = "카테고리 내 메뉴 삭제", description = "사장님과 관리자는 메뉴를 삭제할 수 있습니다.")
	@DeleteMapping("/{menuCategoryId}/items/{menuCategoryItemId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<BaseResponseDto<Void>> removeItem(
		@Parameter(description = "가게 UUID") @PathVariable UUID storeId,
		@Parameter(description = "메뉴 카테고리 UUID") @PathVariable UUID menuCategoryId,
		@Parameter(description = "메뉴 카테고리 아이템 UUID") @PathVariable UUID menuCategoryItemId,
		@AuthenticationPrincipal UserAuth user) {
		menuCategoryItemService.removeMenuFromCategory(menuCategoryItemId, user);
		return ResponseEntity.ok(BaseResponseDto.success("카테고리 메뉴 삭제 성공"));
	}
}
