package com.sparta.dingdong.domain.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.AI.service.AIService;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.menu.dto.request.MenuItemRequestDto;
import com.sparta.dingdong.domain.menu.dto.response.MenuItemResponseDto;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.exception.DeletedStoreMenuAccessException;
import com.sparta.dingdong.domain.menu.exception.HiddenMenuAccessDeniedException;
import com.sparta.dingdong.domain.menu.exception.MenuItemNotFoundException;
import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.store.exception.StoreNotFoundException;
import com.sparta.dingdong.domain.store.repository.StoreRepository;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

	private final MenuItemRepository menuItemRepository;
	private final StoreRepository storeRepository;
	private final AIService aiMenuDescriptionService;
	private final AuthService authService;

	@Transactional(readOnly = true)
	@Override
	public List<MenuItemResponseDto> getAllByStore(UUID storeId, boolean includeHidden, UserAuth user) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);

		// 해당 가게가 삭제됐으면 메뉴 조회되면 안됨
		if (store.getDeletedAt() != null) {
			throw new DeletedStoreMenuAccessException();
		}

		List<MenuItem> items;

		if (includeHidden) {
			// user가 null이면 숨김 메뉴 조회 불가
			if (user == null) {
				throw new SecurityException("로그인이 필요합니다.");
			}

			UserRole role = user.getUserRole();

			switch (role) {
				case MASTER, MANAGER -> items = menuItemRepository.findAllByStoreIdIncludingDeleted(storeId);
				case OWNER -> {
					authService.validateStoreOwnership(user, store.getOwner().getId());
					items = menuItemRepository.findAllByStoreIdIncludingDeleted(storeId);
				}
				default -> throw new HiddenMenuAccessDeniedException();
			}

		} else {
			// 모든 사용자(비회원 포함)
			items = menuItemRepository.findActiveByStoreId(storeId);
		}

		return items.stream()
			.map(this::map)
			.toList();
	}

	@Override
	public MenuItemResponseDto create(UUID storeId, MenuItemRequestDto req, UserAuth user) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);
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

		return map(menuItemRepository.save(menu));
	}

	@Transactional(readOnly = true)
	@Override
	public MenuItemResponseDto getById(UUID menuId, UserAuth user) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(MenuItemNotFoundException::new);

		authService.validateStoreOwnership(user, menu.getStore().getOwner().getId());
		return map(menu);
	}

	@Override
	public MenuItemResponseDto update(UUID menuId, MenuItemRequestDto req, UserAuth user) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(MenuItemNotFoundException::new);

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
	public void delete(UUID menuId, UserAuth user) {
		MenuItem menu = menuItemRepository.findById(menuId)
			.orElseThrow(MenuItemNotFoundException::new);

		authService.validateStoreOwnership(user, menu.getStore().getOwner().getId());

		menu.softDelete(user.getId());
		menu.setIsDisplayed(false); // 삭제 시 비노출
		menu.setIsActive(false); // 삭제 시 비활성화
	}

	private MenuItemResponseDto map(MenuItem m) {
		return MenuItemResponseDto.builder()
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
