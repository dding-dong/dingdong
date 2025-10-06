package com.sparta.dingdong.domain.menu.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.AI.service.AIService;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.menu.dto.MenuItemDto;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

	private final MenuItemRepository menuItemRepository;
	private final StoreRepository storeRepository;
	private final AIService aiMenuDescriptionService; // AI 자동 설명용
	private final AuthService authService;

	@Transactional(readOnly = true)
	@Override
	public List<MenuItemDto.Response> getAllByStore(UUID storeId, boolean includeHidden) {
		List<MenuItem> items = includeHidden
			? menuItemRepository.findAllByStoreIdIncludingDeleted(storeId)
			: menuItemRepository.findActiveByStoreId(storeId);

		return items.stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public MenuItemDto.Response create(UUID storeId, MenuItemDto.Request req) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, store.getOwner().getId());

		String aiContent = null;
		boolean usedAi = false;

		if (Boolean.TRUE.equals(req.getUseAi())) {
			aiContent = aiMenuDescriptionService.generateDescription(req.getName(), req.getContent());
			usedAi = true;
		}

		MenuItem menu = MenuItem.builder()
			.store(store)
			.name(req.getName())
			.content(req.getContent())
			.price(req.getPrice())
			.imageUrl(req.getImageUrl())
			.isRecommended(req.getIsRecommended())
			.isDisplayed(req.getIsDisplayed())
			.isSoldout(req.getIsSoldout())
			.aiContent(aiContent)
			.isAiUsed(usedAi)
			.build();

		MenuItem saved = menuItemRepository.save(menu);
		return map(saved);
	}

	@Transactional(readOnly = true)
	@Override
	public MenuItemDto.Response getById(UUID menuId) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, menu.getStore().getOwner().getId());
		return map(menu);
	}

	@Override
	public MenuItemDto.Response update(UUID menuId, MenuItemDto.Request req) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, menu.getStore().getOwner().getId());
		menu.setName(req.getName());
		menu.setContent(req.getContent());
		menu.setPrice(req.getPrice());
		menu.setImageUrl(req.getImageUrl());
		menu.setIsDisplayed(req.getIsDisplayed());
		menu.setIsRecommended(req.getIsRecommended());
		menu.setIsSoldout(req.getIsSoldout());

		if (Boolean.TRUE.equals(req.getUseAi())) {
			String aiDesc = aiMenuDescriptionService.generateDescription(req.getName(), req.getContent());
			menu.setAiContent(aiDesc);
			menu.setIsAiUsed(true);
		}

		return map(menu);
	}

	@Override
	public void delete(UUID menuId) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, menu.getStore().getOwner().getId());
		menu.softDelete(user.getId());
		menu.setIsDisplayed(false); // 삭제 시 해당 메뉴아이템 비노출
	}

	/* ==================== 유틸 메서드 ==================== */

	private MenuItemDto.Response map(MenuItem m) {
		return MenuItemDto.Response.builder()
			.id(m.getId())
			.storeId(m.getStore().getId())
			.name(m.getName())
			.content(m.getContent())
			.price(m.getPrice())
			.imageUrl(m.getImageUrl())
			.isDisplayed(m.getIsDisplayed())
			.isRecommended(m.getIsRecommended())
			.isSoldout(m.getIsSoldout())
			.isAiUsed(m.getIsAiUsed())
			.aiContent(m.getAiContent())
			.build();
	}
}