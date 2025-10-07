package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.category.dto.request.MenuCategoryItemRequestDto;
import com.sparta.dingdong.domain.category.dto.response.MenuCategoryItemResponseDto;

public interface MenuCategoryItemService {
	@Transactional(readOnly = true)
	List<MenuCategoryItemResponseDto> getItemsByCategory(UUID categoryId);

	MenuCategoryItemResponseDto addMenuToCategory(UUID categoryId, MenuCategoryItemRequestDto req);

	void removeMenuFromCategory(UUID menuCategoryItemId);
}
