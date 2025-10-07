package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryResponseDto;
import com.sparta.dingdong.domain.category.entity.MenuCategory;
import com.sparta.dingdong.domain.category.repository.MenuCategoryRepository;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

	private final MenuCategoryRepository menuCategoryRepository;
	private final StoreRepository storeRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	@Override
	public List<MenuCategoryResponseDto> getByStore(UUID storeId) {
		return menuCategoryRepository.findByStoreIdAndDeletedAtIsNullOrderBySortMenuCategoryAsc(storeId)
			.stream()
			.map(this::map)
			.collect(Collectors.toList());
	}

	@Override
	public MenuCategoryResponseDto create(UUID storeId, MenuCategoryRequestDto req, UserAuth user) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + storeId));
		authService.validateStoreOwnership(user, store.getOwner().getId());

		boolean exists = menuCategoryRepository.existsByStoreAndSortMenuCategory(store, req.getSortMenuCategory());
		if (exists) {
			throw new IllegalArgumentException("이미 해당 정렬 순서를 가진 카테고리가 존재합니다.");
		}

		MenuCategory mc = MenuCategory.builder()
			.store(store)
			.name(req.getName())
			.sortMenuCategory(req.getSortMenuCategory())
			.build();

		MenuCategory saved = menuCategoryRepository.save(mc);
		return map(saved);
	}

	@Override
	public MenuCategoryResponseDto update(UUID menuCategoryId, MenuCategoryRequestDto req, UserAuth user) {
		MenuCategory mc = menuCategoryRepository.findByIdWithStore(menuCategoryId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴 카테고리를 찾을 수 없습니다: " + menuCategoryId));
		authService.validateStoreOwnership(user, mc.getStore().getOwner().getId());
		mc.setName(req.getName());
		mc.setSortMenuCategory(req.getSortMenuCategory());
		return map(mc);
	}

	@Override
	public void delete(UUID menuCategoryId, UserAuth user) {
		MenuCategory mc = menuCategoryRepository.findByIdWithStore(menuCategoryId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴 카테고리를 찾을 수 없습니다: " + menuCategoryId));
		authService.validateStoreOwnership(user, mc.getStore().getOwner().getId());

		mc.softDelete(user.getId());
	}

	@Override
	public MenuCategoryResponseDto restore(UUID menuCategoryId, UserAuth user) {
		MenuCategory mc = menuCategoryRepository.findDeletedById(menuCategoryId)
			.orElseThrow(() -> new IllegalArgumentException("삭제된 메뉴 카테고리를 찾을 수 없습니다: " + menuCategoryId));
		mc.restore(user.getId());
		return map(mc);
	}

	private MenuCategoryResponseDto map(MenuCategory mc) {
		return MenuCategoryResponseDto.builder()
			.id(mc.getId())
			.storeId(mc.getStore() != null ? mc.getStore().getId() : null)
			.name(mc.getName())
			.sortMenuCategory(mc.getSortMenuCategory())
			.build();
	}
}
