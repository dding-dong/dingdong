package com.sparta.dingdong.domain.menu.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.UserRepository;

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
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public Page<MenuItemResponseDto> getAllByStore(UUID storeId, boolean includeHidden, String keyword,
		Pageable pageable, UserAuth user) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);
		// 해당 가게가 삭제됐으면 메뉴 조회되면 안됨
		if (store.getDeletedAt() != null) {
			throw new DeletedStoreMenuAccessException();
		}

		boolean includeDeleted = false;

		if (includeHidden) {
			if (user == null)
				throw new SecurityException("로그인이 필요합니다.");

			UserRole role = user.getUserRole();
			switch (role) {
				case MASTER, MANAGER -> includeDeleted = true;
				case OWNER -> {
					authService.validateStoreOwnership(user, store.getOwner().getId());
					includeDeleted = true;
				}
				default -> throw new HiddenMenuAccessDeniedException();
			}
		}

		Page<MenuItem> itemsPage = menuItemRepository.findByStoreIdWithKeyword(storeId, keyword, pageable,
			includeDeleted);

		return itemsPage.map(this::map);
	}

	@Override
	public MenuItemResponseDto create(UUID storeId, MenuItemRequestDto req, UserAuth user) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);
		authService.validateStoreOwnership(user, store.getOwner().getId());

		User currentUser = userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		String aiContent = null;
		boolean usedAi = false;
		if (Boolean.TRUE.equals(req.getUseAi())) {
			aiContent = aiMenuDescriptionService.generateDescription(req.getName(), req.getContent(), currentUser);
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

		User currentUser = userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		menu.setName(req.getName());
		menu.setContent(req.getContent());
		menu.setPrice(req.getPrice());
		menu.setImageUrl(req.getImageUrl());
		menu.setIsDisplayed(req.getIsDisplayed());
		menu.setIsRecommended(req.getIsRecommended());
		menu.setIsSoldout(req.getIsSoldout());

		if (Boolean.TRUE.equals(req.getUseAi())) {
			String aiDesc = aiMenuDescriptionService.generateDescription(req.getName(), req.getContent(), currentUser);
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
