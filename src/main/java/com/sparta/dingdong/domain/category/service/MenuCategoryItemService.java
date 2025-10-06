package com.sparta.dingdong.domain.category.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.category.dto.MenuCategoryDto;

public interface MenuCategoryItemService {
	List<MenuCategoryDto.ItemResponse> getItemsByCategory(UUID categoryId);

	MenuCategoryDto.ItemResponse addMenuToCategory(UUID categoryId, MenuCategoryDto.ItemRequest req);

	void removeMenuFromCategory(UUID categoryItemId);
}
