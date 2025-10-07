package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.dto.request.MenuCategoryItemRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryItemResponseDto;
import com.sparta.dingdong.domain.category.entity.MenuCategory;
import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;
import com.sparta.dingdong.domain.category.repository.MenuCategoryItemRepository;
import com.sparta.dingdong.domain.category.repository.MenuCategoryRepository;
import com.sparta.dingdong.domain.menu.entity.MenuItem;
import com.sparta.dingdong.domain.menu.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCategoryItemServiceImpl implements MenuCategoryItemService {

	private final MenuCategoryRepository menuCategoryRepository;
	private final MenuCategoryItemRepository menuCategoryItemRepository;
	private final MenuItemRepository menuItemRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	@Override
	public List<MenuCategoryItemResponseDto> getItemsByCategory(UUID menuCategoryId) {
		return menuCategoryItemRepository.findByMenuCategoryIdOrderByOrderNoAsc(menuCategoryId)
			.stream()
			.map(this::map)
			.collect(Collectors.toList());
	}

	@Override
	public MenuCategoryItemResponseDto addMenuToCategory(UUID categoryId, MenuCategoryItemRequestDto req,
		UserAuth user) {
		MenuCategory mc = menuCategoryRepository.findByIdWithStore(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴 카테고리를 찾을 수 없습니다: " + categoryId));
		authService.validateStoreOwnership(user, mc.getStore().getOwner().getId());
		MenuItem item = menuItemRepository.findById(req.getMenuItemId())
			.orElseThrow(() -> new IllegalArgumentException("메뉴 아이템을 찾을 수 없습니다: " + req.getMenuItemId()));
		boolean exists = menuCategoryItemRepository.existsByMenuCategoryIdAndOrderNo(categoryId, req.getOrderNo());
		if (exists) {
			throw new IllegalArgumentException("해당 카테고리에서 이미 같은 순서(orderNo)의 메뉴가 존재합니다.");
		}
		MenuCategoryItem mci = MenuCategoryItem.builder()
			.menuCategory(mc)
			.menuItem(item)
			.orderNo(req.getOrderNo())
			.build();
		MenuCategoryItem saved = menuCategoryItemRepository.save(mci);
		return map(saved);
	}

	@Override
	public void removeMenuFromCategory(UUID menuCategoryItemId, UserAuth user) {
		MenuCategoryItem mci = menuCategoryItemRepository.findByIdWithMenuCategoryAndStore(menuCategoryItemId)
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리의 메뉴아이템이 존재하지 않습니다: " + menuCategoryItemId));
		authService.validateStoreOwnership(user, mci.getMenuCategory().getStore().getOwner().getId());
		menuCategoryItemRepository.delete(mci);
	}

	private MenuCategoryItemResponseDto map(MenuCategoryItem mci) {
		return MenuCategoryItemResponseDto.builder()
			.id(mci.getId())
			.menuCategoryId(mci.getMenuCategory().getId())
			.menuItemId(mci.getMenuItem().getId())
			.menuItemName(mci.getMenuItem().getName())
			.menuItemPrice(mci.getMenuItem().getPrice())
			.orderNo(mci.getOrderNo())
			.build();
	}
}
