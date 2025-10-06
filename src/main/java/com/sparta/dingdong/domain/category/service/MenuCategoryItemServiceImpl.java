package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.service.AuthService;
import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;
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

	@Override
	@Transactional(readOnly = true)
	public List<MenuCategoryDto.ItemResponse> getItemsByCategory(UUID categoryId) {
		return menuCategoryItemRepository.findByMenuCategoryIdOrderByOrderNoAsc(categoryId)
			.stream()
			.map(this::map)
			.collect(Collectors.toList());
	}

	@Override
	public MenuCategoryDto.ItemResponse addMenuToCategory(UUID categoryId, MenuCategoryDto.ItemRequest req) {
		// MenuCategory + Store fetch join
		MenuCategory mc = menuCategoryRepository.findByIdWithStore(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("메뉴 카테고리를 찾을 수 없습니다: " + categoryId));

		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, mc.getStore().getOwner().getId());

		MenuItem item = menuItemRepository.findById(req.getMenuItemId())
			.orElseThrow(() -> new IllegalArgumentException("메뉴 아이템을 찾을 수 없습니다: " + req.getMenuItemId()));

		// orderNo 중복 체크
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
	public void removeMenuFromCategory(UUID menuCategoryItemId) {
		// MenuCategoryItem -> MenuCategory -> Store fetch join
		MenuCategoryItem mci = menuCategoryItemRepository.findByIdWithMenuCategoryAndStore(menuCategoryItemId)
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리의 메뉴아이템이 존재하지 않습니다: " + menuCategoryItemId));

		UserAuth user = authService.getCurrentUser();
		authService.validateStoreOwnership(user, mci.getMenuCategory().getStore().getOwner().getId());

		menuCategoryItemRepository.delete(mci);
	}

	/* ==================== 유틸 메서드 ==================== */

	private MenuCategoryDto.ItemResponse map(MenuCategoryItem mci) {
		// fetch join + DB 제약조건으로 menuCategory/menuItem은 항상 존재
		return MenuCategoryDto.ItemResponse.builder()
			.id(mci.getId())
			.menuCategoryId(mci.getMenuCategory().getId())
			.menuItemId(mci.getMenuItem().getId())
			.menuItemName(mci.getMenuItem().getName())
			.menuItemPrice(mci.getMenuItem().getPrice())
			.orderNo(mci.getOrderNo())
			.build();
	}
}
